package com.boxing.gestioncanina.ui.dashboard

import PetsAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.network.Supabase
import com.boxing.gestioncanina.models.AdoptionPet
import com.boxing.gestioncanina.models.AdoptionPetUI
import com.boxing.gestioncanina.data.model.Pet
import com.boxing.gestioncanina.models.PetSupabase
import com.boxing.gestioncanina.ui.medical.Consultas_Veterinaria
import com.boxing.gestioncanina.ui.settings.ProfileFragment
import com.boxing.gestioncanina.ui.settings.SettingsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    // RecyclerViews
    private lateinit var myPetsRecyclerView: RecyclerView
    private lateinit var adoptionRecyclerView: RecyclerView
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var adoptionAdapter: AdoptionAdapter

    // Header views
    private lateinit var notificationButton: FrameLayout
    private lateinit var notificationDot: View
    private lateinit var profileImageCard: CardView
    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        loadUserName()
        setupMyPetsRecyclerView()
        setupAdoptionRecyclerView()
        setupClickListeners()
        setupBottomMenu()

        // Cargar datos
        loadMyPetsFromSupabase()
        loadAdoptionPetsFromSupabase()
    }

    // -----------------------------------------------------------
    // INIT VIEWS
    // -----------------------------------------------------------
    private fun initViews() {
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
    }

    // -----------------------------------------------------------
    // BOTTOM MENU
    // -----------------------------------------------------------
    private fun setupBottomMenu() {
        navInicio.setOnClickListener {
            // Ya estamos en Dashboard, no hacer nada
        }

        navConsulta.setOnClickListener {
            startActivity(Intent(this, Consultas_Veterinaria::class.java))
        }

        navSetting.setOnClickListener {
            startActivity(Intent(this, SettingsFragment::class.java))
        }
    }

    // -----------------------------------------------------------
    // CARGAR NOMBRE DEL USUARIO DESDE SUPABASE
    // -----------------------------------------------------------
    private fun loadUserName() {
        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            userName.text = "Usuario"
            return
        }

        val email = user.email ?: "Usuario"
        userName.text = email.substringBefore("@")
    }

    // -----------------------------------------------------------
    // PETS RECYCLERVIEW (MIS MASCOTAS)
    // -----------------------------------------------------------
    private fun setupMyPetsRecyclerView() {
        petsAdapter = PetsAdapter(
            pets = mutableListOf(),
            onAddClick = {
                showAddPetModal()
            },
            onPetClick = { pet ->
                Toast.makeText(this, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
                // Aquí puedes navegar a la pantalla de detalles de la mascota
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
    }

    // -----------------------------------------------------------
    // ADOPTION RECYCLERVIEW
    // -----------------------------------------------------------
    private fun setupAdoptionRecyclerView() {
        adoptionAdapter = AdoptionAdapter(
            pets = mutableListOf(),
            onAdoptClick = { pet ->
                Toast.makeText(this, "Deseas adoptar a: ${pet.name}", Toast.LENGTH_SHORT).show()
                // Aquí puedes abrir un diálogo de confirmación o navegar a detalles
            }
        )

        adoptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 2)
            adapter = adoptionAdapter
        }
    }

    // -----------------------------------------------------------
    // LOAD MY PETS (MASCOTAS DEL USUARIO)
    // -----------------------------------------------------------
    private fun loadMyPetsFromSupabase() {
        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Consultar mascotas del usuario actual
                val response = Supabase.client
                    .from("pets")
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }
                    .decodeList<PetSupabase>()

                // Mapear a modelo Pet
                val pets = response.map {
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
                        println("ℹ️ No hay mascotas registradas para este usuario")
                    } else {
                        println("✅ Se cargaron ${pets.size} mascotas")
                    }
                    petsAdapter.updatePets(pets)
                }

            } catch (e: Exception) {
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = Supabase.client
                    .from("adoption_pets")
                    .select(Columns.ALL)
                    .decodeList<AdoptionPetUI>()

                val pets = response
                    .filter { !it.is_adopted }
                    .map {
                        AdoptionPet(
                            id = it.id,
                            name = it.name,
                            breed = it.breed,
                            imageUrl = it.image_url ?: "",
                            age = it.age
                        )
                    }

                withContext(Dispatchers.Main) {
                    if (pets.isEmpty()) {
                        println("ℹ️ No hay mascotas en adopción disponibles")
                    } else {
                        println("✅ Se cargaron ${pets.size} mascotas en adopción")
                    }
                    adoptionAdapter.updatePets(pets)
                }

            } catch (e: Exception) {
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

    // ADD PET MODAL
// -----------------------------------------------------------
    private fun showAddPetModal() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.modal_add_pet, null)

        // Referencias a los views
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val photoCard: CardView = view.findViewById(R.id.photoCard)
        val petPhotoPreview: ImageView = view.findViewById(R.id.petPhotoPreview)
        val petNameInput: TextInputEditText = view.findViewById(R.id.petNameInput)
        val petBreedInput: TextInputEditText = view.findViewById(R.id.petBreedInput)
        val petAgeInput: TextInputEditText = view.findViewById(R.id.petAgeInput)
        val saveButton: CardView = view.findViewById(R.id.saveButton)

        // Cerrar modal
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // TODO: Agregar lógica para seleccionar foto
        photoCard.setOnClickListener {
            Toast.makeText(
                this,
                "Seleccionar foto (implementar ImagePicker)",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Guardar mascota
        saveButton.setOnClickListener {

            // 🔑 FORZAR CIERRE DE TECLADO Y FOCO (CLAVE)
            petAgeInput.clearFocus()
            petBreedInput.clearFocus()
            petNameInput.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            // Leer valores de forma segura
            val name = petNameInput.text?.toString()?.trim() ?: ""
            val breed = petBreedInput.text?.toString()?.trim() ?: ""
            val ageText = petAgeInput.text?.toString()?.trim() ?: ""

            // Validaciones
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa el nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (breed.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa la raza", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa la edad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age < 0) {
                Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar en Supabase
            savePetToSupabase(name, breed, age, dialog)
        }

        dialog.setContentView(view)
        dialog.show()
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
        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newPet = InsertPet(
                    user_id = user.id,
                    name = name,
                    breed = breed,
                    age = age,
                    image_url = null
                )

                Supabase.client
                    .from("pets")
                    .insert(newPet)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "¡Mascota agregada exitosamente!",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                    loadMyPetsFromSupabase()
                }

            } catch (e: Exception) {
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
        // Perfil
        profileImageCard.setOnClickListener {
            startActivity(Intent(this, ProfileFragment::class.java))
        }

        // Notificaciones
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
        }

        // Búsqueda y filtros
        filterButton.setOnClickListener {
            Toast.makeText(this, "Filtros no implementados", Toast.LENGTH_SHORT).show()
        }

        // Ver todas las mascotas
        viewAllMyPets.setOnClickListener {
            Toast.makeText(this, "Ver todas mis mascotas", Toast.LENGTH_SHORT).show()
            // Aquí puedes navegar a una pantalla con todas las mascotas
        }

        // Ver todas las adopciones
        viewAllAdoptionPets.setOnClickListener {
            Toast.makeText(this, "Ver todas las adopciones", Toast.LENGTH_SHORT).show()
            // Aquí puedes navegar a una pantalla con todas las adopciones
        }

        // Cards de acciones rápidas
        findPartnerCard.setOnClickListener {
            Toast.makeText(this, "Encontrar pareja para mascota", Toast.LENGTH_SHORT).show()
        }

        adoptPetCard.setOnClickListener {
            Toast.makeText(this, "Ver mascotas en adopción", Toast.LENGTH_SHORT).show()
        }

        // Servicios
        veterinaryCard.setOnClickListener {
            startActivity(Intent(this, Consultas_Veterinaria::class.java))
        }

        groomingCard.setOnClickListener {
            Toast.makeText(this, "Peluquería canina", Toast.LENGTH_SHORT).show()
        }

        storeCard.setOnClickListener {
            Toast.makeText(this, "Tienda de productos", Toast.LENGTH_SHORT).show()
        }

        // Banner promocional
        promoBanner.setOnClickListener {
            Toast.makeText(this, "¡Aprovecha la oferta especial!", Toast.LENGTH_SHORT).show()
        }
    }
}