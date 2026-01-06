package com.boxing.gestioncanina.data.repository

import com.boxing.gestioncanina.data.model.RegisterRequest
import com.boxing.gestioncanina.data.model.RegisterResult
import com.boxing.gestioncanina.data.model.User
import com.boxing.gestioncanina.data.network.Supabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun registerUser(request: RegisterRequest): RegisterResult = withContext(Dispatchers.IO) {
        try {
            // Registro en Supabase Auth
            Supabase.client.auth.signUpWith(Email) {
                email = request.email
                password = request.password
            }

            // Obtener ID del usuario recién creado
            val userId = Supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext RegisterResult(
                    success = false,
                    message = "Error al obtener el ID del usuario"
                )

            // Crear objeto User
            val user = User(
                id = userId,
                name = request.name,
                email = request.email,
                location = request.location,
                createdAt = System.currentTimeMillis()
            )

            // Insertar en la tabla users usando el modelo serializado
            Supabase.client.from("users").insert(user)

            RegisterResult(
                success = true,
                message = "Registro exitoso",
                user = user
            )

        } catch (e: Exception) {
            RegisterResult(
                success = false,
                message = e.message ?: "Error desconocido al registrar usuario"
            )
        }
    }

    suspend fun loginUser(email: String, password: String): RegisterResult = withContext(Dispatchers.IO) {
        try {
            Supabase.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = Supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext RegisterResult(
                    success = false,
                    message = "Error al obtener usuario"
                )

            // Obtener datos del usuario desde la tabla
            val userData = Supabase.client.from("users")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingle<User>()

            RegisterResult(
                success = true,
                message = "Inicio de sesión exitoso",
                user = userData
            )

        } catch (e: Exception) {
            RegisterResult(
                success = false,
                message = e.message ?: "Error al iniciar sesión"
            )
        }
    }

    suspend fun signOut() {
        try {
            Supabase.client.auth.signOut()
        } catch (e: Exception) {
            // Log error
        }
    }
}