package com.boxing.gestioncanina.ui.settings

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.network.Supabase
import com.boxing.gestioncanina.ui.dashboard.DashboardActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

// 🔹 Modelo de datos para la tabla profiles
@Serializable
data class UserProfile(
    val id: String,
    val full_name: String? = null,
    val phone: String? = null,
    val birth_date: String? = null,
    val profile_image_url: String? = null,
    val notifications_enabled: Boolean = true,
    val dark_mode_enabled: Boolean = false,
    val two_factor_enabled: Boolean = false
)

// 🔹 Modelo para actualizar perfil
@Serializable
data class UpdateProfile(
    val full_name: String? = null,
    val phone: String? = null,
    val birth_date: String? = null,
    val profile_image_url: String? = null,
    val notifications_enabled: Boolean? = null,
    val dark_mode_enabled: Boolean? = null,
    val two_factor_enabled: Boolean? = null
)

class ProfileFragment : AppCompatActivity() {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    // UI Elements
    private lateinit var btnBack: ImageButton
    private lateinit var fabCamera: FloatingActionButton
    private lateinit var imgProfile: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfilePhone: TextView
    private lateinit var tvProfileBirthDate: TextView
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var layoutChangePassword: LinearLayout
    private lateinit var switchTwoFactor: SwitchMaterial
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnSave: MaterialButton

    // Datos del usuario
    private var currentProfile: UserProfile? = null
    private var userId: String? = null
    private var userEmail: String? = null
    private var selectedImageUri: Uri? = null

    // Launcher para seleccionar imagen
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d(TAG, "📷 Imagen de perfil seleccionada: $it")
            selectedImageUri = it

            // Mostrar preview
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(imgProfile)

            Toast.makeText(this, "Foto seleccionada. Presiona Guardar para aplicar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🚀 Iniciando ProfileFragment")
        Log.d(TAG, "════════════════════════════════════════")

        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_fragment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
        loadUserProfile()
    }

    private fun initViews() {
        Log.d(TAG, "🔧 Inicializando views...")

        btnBack = findViewById(R.id.btnBack)
        fabCamera = findViewById(R.id.fabCamera)
        imgProfile = findViewById(R.id.imgProfile)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfilePhone = findViewById(R.id.tvProfilePhone)
        tvProfileBirthDate = findViewById(R.id.tvProfileBirthDate)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        layoutChangePassword = findViewById(R.id.layoutChangePassword)
        switchTwoFactor = findViewById(R.id.switchTwoFactor)
        switchNotifications = findViewById(R.id.switchNotifications)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)

        Log.d(TAG, "✅ Views inicializadas")
    }

    private fun setupListeners() {
        Log.d(TAG, "🔧 Configurando listeners...")

        btnBack.setOnClickListener {
            Log.d(TAG, "👆 Click en volver")
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        fabCamera.setOnClickListener {
            Log.d(TAG, "👆 Click en cambiar foto de perfil")
            imagePickerLauncher.launch("image/*")
        }

        btnEditProfile.setOnClickListener {
            Log.d(TAG, "👆 Click en editar perfil")
            showEditDialog()
        }

        layoutChangePassword.setOnClickListener {
            Log.d(TAG, "👆 Click en cambiar contraseña")
            showChangePasswordDialog()
        }

        btnCancel.setOnClickListener {
            Log.d(TAG, "👆 Click en cancelar - Recargando datos")
            selectedImageUri = null
            loadUserProfile()
        }

        btnSave.setOnClickListener {
            Log.d(TAG, "👆 Click en guardar cambios")
            saveProfile()
        }

        Log.d(TAG, "✅ Listeners configurados")
    }

    private fun loadUserProfile() {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "📡 CARGANDO PERFIL DE USUARIO")
        Log.d(TAG, "════════════════════════════════════════")

        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Log.e(TAG, "❌ Usuario no autenticado")
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userId = user.id
        userEmail = user.email

        Log.d(TAG, "🆔 User ID: $userId")
        Log.d(TAG, "📧 Email: $userEmail")

        lifecycleScope.launch {
            try {
                Log.d(TAG, "🔍 Consultando tabla 'profiles'...")

                val response = Supabase.client
                    .from("profiles")
                    .select {
                        filter {
                            eq("id", userId!!)
                        }
                    }
                    .decodeSingleOrNull<UserProfile>()

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        Log.d(TAG, "✅ Perfil encontrado")
                        currentProfile = response
                        updateUI()
                    } else {
                        Log.w(TAG, "⚠️ Perfil no encontrado, creando uno nuevo...")
                        createDefaultProfile()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cargar perfil: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileFragment,
                        "Error al cargar perfil: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun createDefaultProfile() {
        Log.d(TAG, "🔨 Creando perfil por defecto...")

        lifecycleScope.launch {
            try {
                val defaultProfile = UserProfile(
                    id = userId!!,
                    full_name = "",
                    phone = "",
                    birth_date = "",
                    profile_image_url = null,
                    notifications_enabled = true,
                    dark_mode_enabled = false,
                    two_factor_enabled = false
                )

                Supabase.client
                    .from("profiles")
                    .insert(defaultProfile)

                Log.d(TAG, "✅ Perfil creado exitosamente")

                withContext(Dispatchers.Main) {
                    currentProfile = defaultProfile
                    updateUI()
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al crear perfil: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileFragment,
                        "Error al crear perfil: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateUI() {
        Log.d(TAG, "🎨 Actualizando UI con datos del perfil...")

        currentProfile?.let { profile ->
            // Header
            tvUserName.text = if (profile.full_name.isNullOrEmpty()) {
                userEmail?.substringBefore("@") ?: "Usuario"
            } else {
                profile.full_name
            }
            tvUserEmail.text = userEmail ?: ""

            // Información personal
            tvProfileName.text = profile.full_name?.takeIf { it.isNotEmpty() } ?: "No especificado"
            tvProfilePhone.text = profile.phone?.takeIf { it.isNotEmpty() } ?: "No especificado"
            tvProfileBirthDate.text = profile.birth_date?.takeIf { it.isNotEmpty() } ?: "No especificado"

            // Switches
            switchNotifications.isChecked = profile.notifications_enabled
            switchDarkMode.isChecked = profile.dark_mode_enabled
            switchTwoFactor.isChecked = profile.two_factor_enabled

            // Imagen de perfil
            if (!profile.profile_image_url.isNullOrEmpty()) {
                Log.d(TAG, "📷 Cargando imagen de perfil: ${profile.profile_image_url}")
                Glide.with(this)
                    .load(profile.profile_image_url)
                    .circleCrop()
                    .placeholder(R.drawable.logo_png)
                    .error(R.drawable.logo_png)
                    .into(imgProfile)
            } else {
                Log.d(TAG, "⚠️ Sin imagen de perfil")
                imgProfile.setImageResource(R.drawable.logo_png)
            }

            Log.d(TAG, "✅ UI actualizada")
        }
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etEditName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etEditPhone)
        val etBirthDate = dialogView.findViewById<TextInputEditText>(R.id.etEditBirthDate)

        // Prellenar datos actuales
        etName.setText(currentProfile?.full_name ?: "")
        etPhone.setText(currentProfile?.phone ?: "")
        etBirthDate.setText(currentProfile?.birth_date ?: "")

        // DatePicker para fecha de nacimiento
        etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etBirthDate.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Editar Perfil")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                tvProfileName.text = etName.text.toString().takeIf { it.isNotEmpty() } ?: "No especificado"
                tvProfilePhone.text = etPhone.text.toString().takeIf { it.isNotEmpty() } ?: "No especificado"
                tvProfileBirthDate.text = etBirthDate.text.toString().takeIf { it.isNotEmpty() } ?: "No especificado"

                Toast.makeText(this, "Presiona 'Guardar Cambios' para aplicar", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)

        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        MaterialAlertDialogBuilder(this)
            .setTitle("Cambiar Contraseña")
            .setView(dialogView)
            .setPositiveButton("Cambiar") { _, _ ->
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword.length < 6) {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                changePassword(newPassword)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun changePassword(newPassword: String) {
        Log.d(TAG, "🔐 Cambiando contraseña...")

        lifecycleScope.launch {
            try {
                Supabase.client.auth.updateUser {
                    password = newPassword
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileFragment,
                        "✅ Contraseña actualizada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d(TAG, "✅ Contraseña actualizada")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cambiar contraseña: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileFragment,
                        "Error al cambiar contraseña: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun saveProfile() {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "💾 GUARDANDO PERFIL")
        Log.d(TAG, "════════════════════════════════════════")

        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                var imageUrl = currentProfile?.profile_image_url

                // Subir imagen si fue seleccionada
                if (selectedImageUri != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ProfileFragment,
                            "Subiendo imagen...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    imageUrl = uploadProfileImage(selectedImageUri!!)

                    if (imageUrl == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ProfileFragment,
                                "Error al subir imagen",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@launch
                    }
                }

                // Preparar datos para actualizar
                val updateData = UpdateProfile(
                    full_name = tvProfileName.text.toString().takeIf { it != "No especificado" },
                    phone = tvProfilePhone.text.toString().takeIf { it != "No especificado" },
                    birth_date = tvProfileBirthDate.text.toString().takeIf { it != "No especificado" },
                    profile_image_url = imageUrl,
                    notifications_enabled = switchNotifications.isChecked,
                    dark_mode_enabled = switchDarkMode.isChecked,
                    two_factor_enabled = switchTwoFactor.isChecked
                )

                Log.d(TAG, "📤 Actualizando perfil en Supabase...")
                Log.d(TAG, "   Nombre: ${updateData.full_name}")
                Log.d(TAG, "   Teléfono: ${updateData.phone}")
                Log.d(TAG, "   Fecha: ${updateData.birth_date}")
                Log.d(TAG, "   Imagen: ${updateData.profile_image_url}")

                Supabase.client
                    .from("profiles")
                    .update(updateData) {
                        filter {
                            eq("id", userId!!)
                        }
                    }

                Log.d(TAG, "✅ Perfil actualizado exitosamente")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileFragment,
                        "✅ Perfil actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    selectedImageUri = null
                    loadUserProfile()
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al guardar perfil: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProfileFragment,
                        "Error al guardar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private suspend fun uploadProfileImage(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "📤 Subiendo imagen de perfil a Storage...")

                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    Log.e(TAG, "❌ No se pudieron leer los bytes de la imagen")
                    return@withContext null
                }

                // Nombre del archivo: user_id/profile.jpg
                val fileName = "$userId/profile.jpg"
                Log.d(TAG, "📝 Nombre del archivo: $fileName")

                val bucket = Supabase.client.storage.from("profile-images")

                // Intentar actualizar si ya existe, sino subir nuevo
                try {
                    bucket.update(fileName, bytes)
                    Log.d(TAG, "🔄 Imagen actualizada")
                } catch (e: Exception) {
                    bucket.upload(fileName, bytes)
                    Log.d(TAG, "⬆️ Imagen subida por primera vez")
                }

                val publicUrl = bucket.publicUrl(fileName)
                Log.d(TAG, "✅ URL pública: $publicUrl")

                publicUrl

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al subir imagen: ${e.message}", e)
                null
            }
        }
    }
}