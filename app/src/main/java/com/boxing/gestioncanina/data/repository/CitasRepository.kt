package com.boxing.gestioncanina.data.repository

import com.boxing.gestioncanina.data.models.Cita
import com.boxing.gestioncanina.data.models.Mascota
import com.boxing.gestioncanina.data.network.Supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CitasRepository {

    private val supabase = Supabase.client

    suspend fun crearCita(cita: Cita): Cita = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("Usuario no autenticado")

            val citaConUsuario = cita.copy(usuarioId = userId)

            val resultado = supabase.from("citas")
                .insert(citaConUsuario) {
                    select()
                }
                .decodeSingle<Cita>()

            resultado
        } catch (e: Exception) {
            throw Exception("Error al crear la cita: ${e.message}")
        }
    }

    suspend fun obtenerCitasUsuario(): List<Cita> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("Usuario no autenticado")

            supabase.from("citas")
                .select {
                    filter {
                        eq("usuario_id", userId)
                    }
                }
                .decodeList<Cita>()
                .sortedWith(compareBy({ it.fecha }, { it.hora }))
        } catch (e: Exception) {
            throw Exception("Error al obtener las citas: ${e.message}")
        }
    }

    suspend fun obtenerCitasPorMascota(mascotaId: String): List<Cita> = withContext(Dispatchers.IO) {
        try {
            supabase.from("citas")
                .select {
                    filter {
                        eq("mascota_id", mascotaId)
                    }
                }
                .decodeList<Cita>()
                .sortedByDescending { it.fecha }
        } catch (e: Exception) {
            throw Exception("Error al obtener las citas: ${e.message}")
        }
    }

    suspend fun obtenerCitasProximas(): List<Cita> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("Usuario no autenticado")

            supabase.from("citas")
                .select {
                    filter {
                        eq("usuario_id", userId)
                        eq("estado", "Programada")
                    }
                }
                .decodeList<Cita>()
                .sortedWith(compareBy({ it.fecha }, { it.hora }))
        } catch (e: Exception) {
            throw Exception("Error al obtener las citas próximas: ${e.message}")
        }
    }

    suspend fun actualizarCita(citaId: String, cita: Cita): Cita = withContext(Dispatchers.IO) {
        try {
            supabase.from("citas")
                .update(cita) {
                    filter {
                        eq("id", citaId)
                    }
                    select()
                }
                .decodeSingle<Cita>()
        } catch (e: Exception) {
            throw Exception("Error al actualizar la cita: ${e.message}")
        }
    }

    suspend fun cambiarEstadoCita(citaId: String, nuevoEstado: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.from("citas")
                .update({
                    set("estado", nuevoEstado)
                }) {
                    filter {
                        eq("id", citaId)
                    }
                }
            true
        } catch (e: Exception) {
            throw Exception("Error al cambiar el estado: ${e.message}")
        }
    }

    suspend fun eliminarCita(citaId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.from("citas")
                .delete {
                    filter {
                        eq("id", citaId)
                    }
                }
            true
        } catch (e: Exception) {
            throw Exception("Error al eliminar la cita: ${e.message}")
        }
    }
}

class MascotasRepository {

    private val supabase = Supabase.client

    suspend fun obtenerMascotasUsuario(): List<Mascota> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("Usuario no autenticado")

            supabase.from("mascotas")
                .select {
                    filter {
                        eq("usuario_id", userId)
                    }
                }
                .decodeList<Mascota>()
                .sortedBy { it.nombre }
        } catch (e: Exception) {
            throw Exception("Error al obtener las mascotas: ${e.message}")
        }
    }

    suspend fun obtenerMascotaPorId(mascotaId: String): Mascota? = withContext(Dispatchers.IO) {
        try {
            supabase.from("mascotas")
                .select {
                    filter {
                        eq("id", mascotaId)
                    }
                }
                .decodeSingleOrNull<Mascota>()
        } catch (e: Exception) {
            throw Exception("Error al obtener la mascota: ${e.message}")
        }
    }
}