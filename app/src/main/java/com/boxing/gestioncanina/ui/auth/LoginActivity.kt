package com.boxing.gestioncanina.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.network.Supabase
import com.boxing.gestioncanina.ui.dashboard.DashboardActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogleLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        val txtIniciarSesion = findViewById<TextView>(R.id.txtIniciarSesion)

        // Login con email/password
        btnLogin.setOnClickListener {
            loginUser()
        }

        // Login con Google (placeholder por ahora)
        btnGoogleLogin.setOnClickListener {
            Toast.makeText(this, "Login con Google próximamente", Toast.LENGTH_SHORT).show()
        }

        // Navegar a registro
        txtIniciarSesion.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validaciones
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Deshabilitar botón durante la petición
        btnLogin.isEnabled = false
        btnLogin.text = "Iniciando sesión..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Login con Supabase
                Supabase.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Verificar que el usuario esté autenticado
                val user = Supabase.client.auth.currentUserOrNull()

                if (user != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Ir al Dashboard
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error al iniciar sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                        resetButton()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = when {
                        e.message?.contains("Invalid login credentials") == true ->
                            "Email o contraseña incorrectos"
                        e.message?.contains("Email not confirmed") == true ->
                            "Por favor verifica tu email antes de iniciar sesión"
                        e.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet"
                        else -> "Error: ${e.message}"
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                    resetButton()
                }
            }
        }
    }

    private fun resetButton() {
        btnLogin.isEnabled = true
        btnLogin.text = "Iniciar Sesión"
    }
}