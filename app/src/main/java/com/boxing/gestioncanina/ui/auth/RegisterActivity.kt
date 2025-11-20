package com.boxing.gestioncanina.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boxing.gestioncanina.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etLocation: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Inicializa Firebase
        auth = FirebaseAuth.getInstance()

        // ✅ DESACTIVAR reCAPTCHA para desarrollo
        try {
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
            Log.d("RegisterActivity", "reCAPTCHA desactivado para pruebas")
        } catch (e: Exception) {
            Log.w("RegisterActivity", "No se pudo desactivar reCAPTCHA: ${e.message}")
        }

        db = FirebaseFirestore.getInstance()

        // 🔹 Inicializa vistas
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etLocation = findViewById(R.id.etLocation)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // 🔹 Validaciones básicas
        if (name.isEmpty() || email.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔹 Deshabilitar botón mientras procesa
        btnRegister.isEnabled = false
        btnRegister.text = "Registrando..."

        // 🔹 Registrar con Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    Log.d("RegisterActivity", "Usuario creado exitosamente: $userId")

                    // 🔹 Guardar datos adicionales en Firestore
                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "location" to location,
                        "createdAt" to System.currentTimeMillis()
                    )

                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Log.d("RegisterActivity", "Datos guardados en Firestore")
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                                // 🔹 Ir a Login
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegisterActivity", "Error guardando datos", e)
                                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()

                                // Reactivar botón
                                btnRegister.isEnabled = true
                                btnRegister.text = "Registrar"
                            }
                    }
                } else {
                    val errorCode = (task.exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                    val errorMessage = when (errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> "Este email ya está registrado"
                        "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil"
                        "ERROR_INVALID_EMAIL" -> "Email inválido"
                        else -> "Error en el registro: ${task.exception?.message}"
                    }

                    Log.e("RegisterActivity", "Error en registro: $errorCode", task.exception)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

                    // Reactivar botón
                    btnRegister.isEnabled = true
                    btnRegister.text = "Registrar"
                }
            }
    }
}