package com.boxing.gestioncanina.ui.dashboard

import PetsAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.network.Supabase
import com.boxing.gestioncanina.models.AdoptionPet
import com.boxing.gestioncanina.data.models.AdoptionPetUI
import com.boxing.gestioncanina.data.model.Pet
import com.boxing.gestioncanina.models.PetSupabase
import com.boxing.gestioncanina.ui.adoption.AdoptionPetsActivity
import com.boxing.gestioncanina.ui.medical.Consultas_Veterinaria
import com.boxing.gestioncanina.ui.settings.ProfileFragment
import com.boxing.gestioncanina.ui.settings.SettingsFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class DashboardActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DashboardActivity"
    }

    // RecyclerViews
    private lateinit var myPetsRecyclerView: RecyclerView
    private lateinit var adoptionRecyclerView: RecyclerView
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var adoptionAdapter: AdoptionAdapter

    // Header views
    private   lateinit var notificationButton: FrameLayout
    private lateinit var notificationDot: View
    private lateinit var profileImageCard: CardView
    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var viewEveryPets: TextView

    // Search
    private lateinit var searchInput: EditText
    private lateinit var filterButton: ImageView

    // Quick cards
    private lateinit var findPartnerCard: CardView
    private lateinit var adoptPetCard: CardView

    // View all buttons
    private lateinit var viewAllMyPets: TextView
    private lateinit var viewAllAdoptionPets: TextView

    // Services
    private lateinit var veterinaryCard: CardView
    private lateinit var groomingCard: CardView
    private lateinit var storeCard: CardView

    // Promo
    private lateinit var promoBanner: CardView

    // Bottom Navigation
    private lateinit var navInicio: LinearLayout
    private lateinit var navConsulta: LinearLayout
    private lateinit var navSetting: LinearLayout

    // Para la selección de foto
    private var selectedImageUri: Uri? = null
    private lateinit var currentPetPhotoPreview: ImageView

    // Launcher para seleccionar imagen
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d(TAG, "📷 Imagen seleccionada: $it")
            selectedImageUri = it

            // Mostrar la imagen seleccionada en el preview
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(currentPetPhotoPreview)

            Toast.makeText(this, "Foto seleccionada", Toast.LENGTH_SHORT).show()
        } ?: run {
            Log.w(TAG, "⚠️ No se seleccionó ninguna imagen")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🚀 onCreate: Iniciando DashboardActivity")
        Log.d(TAG, "════════════════════════════════════════")

        setContentView(R.layout.activity_dashboard)

        try {
            initViews()
            Log.d(TAG, "✅ Views inicializadas correctamente")

            loadUserName()
            Log.d(TAG, "✅ Nombre de usuario cargado")

            setupMyPetsRecyclerView()
            Log.d(TAG, "✅ RecyclerView de mis mascotas configurado")

            setupAdoptionRecyclerView()
            Log.d(TAG, "✅ RecyclerView de adopción configurado")

            setupClickListeners()
            Log.d(TAG, "✅ Click listeners configurados")

            setupBottomMenu()
            Log.d(TAG, "✅ Menú inferior configurado")

            // Cargar datos
            Log.d(TAG, "📡 Iniciando carga de datos desde Supabase...")
            loadMyPetsFromSupabase()
            loadAdoptionPetsFromSupabase()

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR CRÍTICO en onCreate: ${e.message}", e)
            Toast.makeText(this, "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // -----------------------------------------------------------
    // INIT VIEWS
    // -----------------------------------------------------------
    private fun initViews() {
        Log.d(TAG, "🔧 Inicializando views...")

        try {
            viewEveryPets = findViewById(R.id.viewAllMyPets)
            notificationButton = findViewById(R.id.notificationButton)
            notificationDot = findViewById(R.id.notificationDot)
            profileImageCard = findViewById(R.id.profileImageCard)
            profileImage = findViewById(R.id.profileImage)
            userName = findViewById(R.id.userName)

            searchInput = findViewById(R.id.searchInput)
            filterButton = findViewById(R.id.filterButton)

            findPartnerCard = findViewById(R.id.findPartnerCard)
            adoptPetCard = findViewById(R.id.adoptPetCard)

            myPetsRecyclerView = findViewById(R.id.myPetsRecyclerView)
            adoptionRecyclerView = findViewById(R.id.adoptionRecyclerView)

            viewAllMyPets = findViewById(R.id.viewAllMyPets)
            viewAllAdoptionPets = findViewById(R.id.viewAllAdoptionPets)

            veterinaryCard = findViewById(R.id.veterinaryCard)
            groomingCard = findViewById(R.id.groomingCard)
            storeCard = findViewById(R.id.storeCard)

            promoBanner = findViewById(R.id.promoBanner)

            userName.text = ""
            notificationDot.visibility = View.GONE

            navInicio = findViewById(R.id.nav_inicio)
            navConsulta = findViewById(R.id.nav_consulta)
            navSetting = findViewById(R.id.nav_setting)

            Log.d(TAG, "✅ Todos los views encontrados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al inicializar views: ${e.message}", e)
            throw e
        }
    }

    // -----------------------------------------------------------
    // BOTTOM MENU
    // -----------------------------------------------------------
    private fun setupBottomMenu() {
        Log.d(TAG, "🔧 Configurando menú inferior...")

        navInicio.setOnClickListener {
            Log.d(TAG, "👆 Click en Inicio (ya estamos aquí)")
        }

        navConsulta.setOnClickListener {
            Log.d(TAG, "👆 Click en Consulta - Navegando a Consultas_Veterinaria")
            startActivity(Intent(this, Consultas_Veterinaria::class.java))
        }

        navSetting.setOnClickListener {
            Log.d(TAG, "👆 Click en Configuración - Navegando a Settings")
            startActivity(Intent(this, SettingsFragment::class.java))
        }
    }

    // -----------------------------------------------------------
    // CARGAR NOMBRE DEL USUARIO DESDE SUPABASE
    // -----------------------------------------------------------
    private fun loadUserName() {
        Log.d(TAG, "👤 Cargando nombre de usuario...")

        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Log.w(TAG, "⚠️ No hay usuario autenticado")
            userName.text = "Usuario"
            return
        }

        val email = user.email ?: "Usuario"
        val displayName = email.substringBefore("@")
        userName.text = displayName

        Log.d(TAG, "✅ Usuario: $displayName (Email: $email)")
        Log.d(TAG, "🆔 User ID: ${user.id}")
    }

    // -----------------------------------------------------------
    // PETS RECYCLERVIEW (MIS MASCOTAS)
    // -----------------------------------------------------------
    private fun setupMyPetsRecyclerView() {
        Log.d(TAG, "🔧 Configurando RecyclerView de Mis Mascotas...")

        petsAdapter = PetsAdapter(
            pets = mutableListOf(),
            onAddClick = {
                Log.d(TAG, "➕ Click en agregar mascota")
                showAddPetModal()
            },
            onPetClick = { pet ->
                Log.d(TAG, "👆 Click en mascota: ${pet.name}")
                Toast.makeText(this, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
            }
        )

        myPetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@DashboardActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = petsAdapter
        }

        Log.d(TAG, "✅ RecyclerView de Mis Mascotas configurado")
    }

    // -----------------------------------------------------------
    // ADOPTION RECYCLERVIEW
    // -----------------------------------------------------------
    private fun setupAdoptionRecyclerView() {
        Log.d(TAG, "🔧 Configurando RecyclerView de Adopción...")

        adoptionAdapter = AdoptionAdapter(
            pets = mutableListOf(),
            onAdoptClick = { pet ->
                Log.d(TAG, "❤️ Click en adoptar: ${pet.name}")
                Toast.makeText(this, "Deseas adoptar a: ${pet.name}", Toast.LENGTH_SHORT).show()
            }
        )

        adoptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 2)
            adapter = adoptionAdapter
        }

        Log.d(TAG, "✅ RecyclerView de Adopción configurado (Grid 2 columnas)")
    }

    // -----------------------------------------------------------
    // LOAD MY PETS (MASCOTAS DEL USUARIO)
    // -----------------------------------------------------------
    private fun loadMyPetsFromSupabase() {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "📡 CARGANDO MIS MASCOTAS DESDE SUPABASE")
        Log.d(TAG, "════════════════════════════════════════")

        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Log.e(TAG, "❌ Usuario no autenticado - No se pueden cargar mascotas")
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "🆔 User ID: ${user.id}")
        Log.d(TAG, "📧 Email: ${user.email}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔍 Consultando tabla 'pets'...")

                val response = Supabase.client
                    .from("pets")
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }
                    .decodeList<PetSupabase>()

                Log.d(TAG, "📦 Respuesta recibida: ${response.size} registros")

                val pets = response.map {
                    Log.d(TAG, "🐕 Pet ID: ${it.id}, Nombre: ${it.name}, Raza: ${it.breed}, Edad: ${it.age}")
                    Pet(
                        id = it.id,
                        name = it.name,
                        breed = it.breed,
                        age = it.age ?: 0,
                        imageUrl = it.image_url ?: ""
                    )
                }

                withContext(Dispatchers.Main) {
                    if (pets.isEmpty()) {
                        Log.w(TAG, "⚠️ No hay mascotas registradas para este usuario")
                    } else {
                        Log.d(TAG, "✅ Se cargaron ${pets.size} mascotas correctamente")
                    }

                    petsAdapter.updatePets(pets)
                    Log.d(TAG, "🔄 Adapter actualizado con ${pets.size} mascotas")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR al cargar mascotas", e)
                Log.e(TAG, "💥 Tipo de error: ${e.javaClass.simpleName}")
                Log.e(TAG, "💬 Mensaje: ${e.message}")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error al cargar mascotas: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    // -----------------------------------------------------------
    // LOAD ADOPTION PETS
    // -----------------------------------------------------------
    private fun loadAdoptionPetsFromSupabase() {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "📡 CARGANDO MASCOTAS EN ADOPCIÓN")
        Log.d(TAG, "════════════════════════════════════════")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔍 Consultando tabla 'adoption_pets'...")

                val response = Supabase.client
                    .from("adoption_pets")
                    .select(Columns.ALL)
                    .decodeList<AdoptionPetUI>()

                Log.d(TAG, "📦 Respuesta recibida: ${response.size} mascotas totales")

                response.forEachIndexed { index, pet ->
                    Log.d(TAG, "📋 [$index] ID: ${pet.id}")
                    Log.d(TAG, "    Nombre: ${pet.name}")
                    Log.d(TAG, "    Raza: ${pet.breed}")
                    Log.d(TAG, "    Edad: ${pet.age}")
                    Log.d(TAG, "    Adoptada: ${pet.is_adopted}")
                    Log.d(TAG, "    URL Imagen: ${pet.image_url ?: "Sin imagen"}")
                }

                val pets = response
                    .filter { !it.is_adopted }
                    .map {
                        Log.d(TAG, "✅ Mascota disponible: ${it.name} (${it.breed})")
                        AdoptionPet(
                            id = it.id,
                            name = it.name,
                            breed = it.breed,
                            imageUrl = it.image_url ?: "",
                            age = it.age
                        )
                    }

                Log.d(TAG, "🔢 Total mascotas en BD: ${response.size}")
                Log.d(TAG, "🔢 Mascotas no adoptadas: ${pets.size}")

                withContext(Dispatchers.Main) {
                    if (pets.isEmpty()) {
                        Log.w(TAG, "⚠️ No hay mascotas disponibles para adopción")
                        Toast.makeText(
                            this@DashboardActivity,
                            "No hay mascotas disponibles para adopción",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d(TAG, "✅ Se cargaron ${pets.size} mascotas en adopción")
                    }

                    adoptionAdapter.updatePets(pets)
                    Log.d(TAG, "🔄 Adapter de adopción actualizado con ${pets.size} mascotas")
                }

            } catch (e: Exception) {
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "❌ ERROR AL CARGAR MASCOTAS EN ADOPCIÓN")
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "💥 Tipo de error: ${e.javaClass.simpleName}")
                Log.e(TAG, "💬 Mensaje: ${e.message}")
                Log.e(TAG, "📚 Stack trace:", e)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error al cargar adopciones: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    // -----------------------------------------------------------
    // ADD PET MODAL
    // -----------------------------------------------------------
    private fun showAddPetModal() {
        Log.d(TAG, "🔧 Abriendo modal para agregar mascota...")

        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.modal_add_pet, null)

        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val photoCard: CardView = view.findViewById(R.id.photoCard)
        val petPhotoPreview: ImageView = view.findViewById(R.id.petPhotoPreview)
        val petNameInput: TextInputEditText = view.findViewById(R.id.petNameInput)
        val petBreedInput: TextInputEditText = view.findViewById(R.id.petBreedInput)
        val petAgeInput: TextInputEditText = view.findViewById(R.id.petAgeInput)
        val saveButton: CardView = view.findViewById(R.id.saveButton)

        // Guardar referencia al preview actual
        currentPetPhotoPreview = petPhotoPreview

        // Resetear la imagen seleccionada al abrir el modal
        selectedImageUri = null

        closeButton.setOnClickListener {
            Log.d(TAG, "❌ Modal cerrado")
            selectedImageUri = null
            dialog.dismiss()
        }

        photoCard.setOnClickListener {
            Log.d(TAG, "📷 Abriendo selector de imágenes...")
            imagePickerLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            Log.d(TAG, "💾 Intentando guardar mascota...")

            petAgeInput.clearFocus()
            petBreedInput.clearFocus()
            petNameInput.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            val name = petNameInput.text?.toString()?.trim() ?: ""
            val breed = petBreedInput.text?.toString()?.trim() ?: ""
            val ageText = petAgeInput.text?.toString()?.trim() ?: ""

            Log.d(TAG, "📝 Datos ingresados:")
            Log.d(TAG, "   Nombre: '$name'")
            Log.d(TAG, "   Raza: '$breed'")
            Log.d(TAG, "   Edad: '$ageText'")
            Log.d(TAG, "   Imagen: ${if (selectedImageUri != null) "Seleccionada" else "No seleccionada"}")

            if (name.isEmpty()) {
                Log.w(TAG, "⚠️ Nombre vacío")
                Toast.makeText(this, "Por favor ingresa el nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (breed.isEmpty()) {
                Log.w(TAG, "⚠️ Raza vacía")
                Toast.makeText(this, "Por favor ingresa la raza", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.isEmpty()) {
                Log.w(TAG, "⚠️ Edad vacía")
                Toast.makeText(this, "Por favor ingresa la edad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age < 0) {
                Log.w(TAG, "⚠️ Edad inválida: $ageText")
                Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "✅ Validación exitosa, guardando...")
            savePetToSupabase(name, breed, age, dialog)
        }

        dialog.setContentView(view)
        dialog.show()
        Log.d(TAG, "✅ Modal mostrado")
    }

    // -----------------------------------------------------------
    // UPLOAD IMAGE TO SUPABASE STORAGE
    // -----------------------------------------------------------
    private suspend fun uploadImageToSupabase(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "📤 Subiendo imagen a Supabase Storage...")

                // Leer los bytes de la imagen
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    Log.e(TAG, "❌ No se pudieron leer los bytes de la imagen")
                    return@withContext null
                }

                // Generar nombre único para la imagen
                val fileName = "pet_${UUID.randomUUID()}.jpg"
                Log.d(TAG, "📝 Nombre del archivo: $fileName")

                // Subir a Supabase Storage (asume que tienes un bucket llamado 'pets')
                val bucket = Supabase.client.storage.from("pets")
                bucket.upload(fileName, bytes)

                // Obtener la URL pública
                val publicUrl = bucket.publicUrl(fileName)
                Log.d(TAG, "✅ Imagen subida exitosamente: $publicUrl")

                publicUrl
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al subir imagen: ${e.message}", e)
                null
            }
        }
    }

    // -----------------------------------------------------------
    // SAVE PET TO SUPABASE
    // -----------------------------------------------------------
    private fun savePetToSupabase(
        name: String,
        breed: String,
        age: Int,
        dialog: BottomSheetDialog
    ) {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "💾 GUARDANDO MASCOTA EN SUPABASE")
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "📝 Nombre: $name")
        Log.d(TAG, "📝 Raza: $breed")
        Log.d(TAG, "📝 Edad: $age")

        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Log.e(TAG, "❌ Usuario no autenticado")
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "🆔 User ID: ${user.id}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Subir imagen si fue seleccionada
                var imageUrl: String? = null
                if (selectedImageUri != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Subiendo imagen...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    imageUrl = uploadImageToSupabase(selectedImageUri!!)

                    if (imageUrl == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@DashboardActivity,
                                "Error al subir la imagen, se guardará sin foto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                val newPet = InsertPet(
                    user_id = user.id,
                    name = name,
                    breed = breed,
                    age = age,
                    image_url = imageUrl
                )

                Log.d(TAG, "📤 Insertando en tabla 'pets'...")
                Log.d(TAG, "📷 URL de imagen: ${imageUrl ?: "Sin imagen"}")

                Supabase.client
                    .from("pets")
                    .insert(newPet)

                Log.d(TAG, "✅ Mascota guardada exitosamente")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "¡Mascota agregada exitosamente!",
                        Toast.LENGTH_SHORT
                    ).show()

                    selectedImageUri = null
                    dialog.dismiss()
                    Log.d(TAG, "🔄 Recargando lista de mascotas...")
                    loadMyPetsFromSupabase()
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR al guardar mascota", e)
                Log.e(TAG, "💥 Tipo: ${e.javaClass.simpleName}")
                Log.e(TAG, "💬 Mensaje: ${e.message}")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error al guardar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    // -----------------------------------------------------------
    // CLICK LISTENERS
    // -----------------------------------------------------------
    private fun setupClickListeners() {
        Log.d(TAG, "🔧 Configurando click listeners...")

        profileImageCard.setOnClickListener {
            Log.d(TAG, "👆 Click en perfil")
            startActivity(Intent(this, ProfileFragment::class.java))
        }

        notificationButton.setOnClickListener {
            Log.d(TAG, "👆 Click en notificaciones")
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
        }

        filterButton.setOnClickListener {
            Log.d(TAG, "👆 Click en filtros")
            Toast.makeText(this, "Filtros no implementados", Toast.LENGTH_SHORT).show()
        }

        viewAllMyPets.setOnClickListener {
            Log.d(TAG, "👆 Click en ver todas mis mascotas")
            startActivity(Intent(this, MyPetsInfo::class.java))
            Toast.makeText(this, "Ver todas mis mascotas", Toast.LENGTH_SHORT).show()
        }

        viewAllAdoptionPets.setOnClickListener {
            Log.d(TAG, "👆 Click en ver todas las adopciones")
            startActivity(Intent(this, AdoptionPetsActivity::class.java))
            Toast.makeText(this, "Ver todas las adopciones", Toast.LENGTH_SHORT).show()
        }

        findPartnerCard.setOnClickListener {
            Log.d(TAG, "👆 Click en encontrar pareja")
            Toast.makeText(this, "Encontrar pareja para mascota", Toast.LENGTH_SHORT).show()
        }

        adoptPetCard.setOnClickListener {
            Log.d(TAG, "👆 Click en adoptar mascota")
            Toast.makeText(this, "Ver mascotas en adopción", Toast.LENGTH_SHORT).show()
        }

        veterinaryCard.setOnClickListener {
            Log.d(TAG, "👆 Click en veterinaria")
            startActivity(Intent(this, Consultas_Veterinaria::class.java))
        }

        groomingCard.setOnClickListener {
            Log.d(TAG, "👆 Click en peluquería")
            Toast.makeText(this, "Peluquería canina", Toast.LENGTH_SHORT).show()
        }

        storeCard.setOnClickListener {
            Log.d(TAG, "👆 Click en tienda")
            Toast.makeText(this, "Tienda de productos", Toast.LENGTH_SHORT).show()
        }

        promoBanner.setOnClickListener {
            Log.d(TAG, "👆 Click en banner promocional")
            Toast.makeText(this, "¡Aprovecha la oferta especial!", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, "✅ Click listeners configurados")
    }
}