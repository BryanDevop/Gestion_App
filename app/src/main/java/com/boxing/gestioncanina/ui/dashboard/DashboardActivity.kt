package com.boxing.gestioncanina.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R

class DashboardActivity : AppCompatActivity() {

    // RecyclerViews
    private lateinit var myPetsRecyclerView: RecyclerView
    private lateinit var adoptionRecyclerView: RecyclerView
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var adoptionAdapter: AdoptionAdapter

<<<<<<< HEAD
    private lateinit var notificationButton: FrameLayout
=======
    // Header views
    private lateinit var notificationButton: FrameLayout
    private lateinit var notificationDot: View
    private lateinit var profileImageCard: CardView
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView

    // Search
    private lateinit var searchInput: EditText
<<<<<<< HEAD
=======
    private lateinit var filterButton: ImageView

    // Quick action cards
    private lateinit var findPartnerCard: CardView
    private lateinit var adoptPetCard: CardView

    // View all buttons
    private lateinit var viewAllMyPets: TextView
    private lateinit var viewAllAdoptionPets: TextView

    // Service cards
    private lateinit var veterinaryCard: CardView
    private lateinit var groomingCard: CardView
    private lateinit var storeCard: CardView

    // Promo banner
    private lateinit var promoBanner: CardView
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DashboardActivity", "onCreate iniciado")

<<<<<<< HEAD
        initViews()
        setupPetsRecyclerView()
        setupAdoptionRecyclerView()
        setupClickListeners()
    }

    private fun initViews() {
        notificationButton = findViewById(R.id.notificationButton)
        profileImage = findViewById(R.id.profileImage)
        userName = findViewById(R.id.userName)
        searchInput = findViewById(R.id.searchInput)
        petsRecyclerView = findViewById(R.id.petsRecyclerView)
        adoptionRecyclerView = findViewById(R.id.adoptionRecyclerView)

        userName.text = "Bryan Greimi"
=======
        try {
            setContentView(R.layout.activity_dashboard)
            Log.d("DashboardActivity", "Layout establecido")

            // Inicializar vistas
            initViews()
            Log.d("DashboardActivity", "Vistas inicializadas")

            // Configurar listas
            setupMyPetsRecyclerView()
            Log.d("DashboardActivity", "RecyclerView de mis mascotas configurado")

            setupAdoptionRecyclerView()
            Log.d("DashboardActivity", "RecyclerView de adopción configurado")

            // Configurar eventos
            setupClickListeners()
            Log.d("DashboardActivity", "Click listeners configurados")

        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error en onCreate: ${e.message}", e)
            Toast.makeText(this, "Error al cargar el dashboard: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        // Header
        notificationButton = findViewById(R.id.notificationButton)
        notificationDot = findViewById(R.id.notificationDot)
        profileImageCard = findViewById(R.id.profileImageCard)
        profileImage = findViewById(R.id.profileImage)
        userName = findViewById(R.id.userName)

        // Search
        searchInput = findViewById(R.id.searchInput)
        filterButton = findViewById(R.id.filterButton)

        // Quick action cards
        findPartnerCard = findViewById(R.id.findPartnerCard)
        adoptPetCard = findViewById(R.id.adoptPetCard)

        // RecyclerViews
        myPetsRecyclerView = findViewById(R.id.myPetsRecyclerView)
        adoptionRecyclerView = findViewById(R.id.adoptionRecyclerView)

        // View all buttons
        viewAllMyPets = findViewById(R.id.viewAllMyPets)
        viewAllAdoptionPets = findViewById(R.id.viewAllAdoptionPets)

        // Service cards
        veterinaryCard = findViewById(R.id.veterinaryCard)
        groomingCard = findViewById(R.id.groomingCard)
        storeCard = findViewById(R.id.storeCard)

        // Promo banner
        promoBanner = findViewById(R.id.promoBanner)

        // Establecer nombre de usuario
        userName.text = "Bryan JT"

        // Mostrar punto de notificación (puedes ocultarlo con notificationDot.visibility = View.GONE)
        notificationDot.visibility = View.VISIBLE
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
    }

    private fun setupMyPetsRecyclerView() {
        val myPets = mutableListOf(
            Pet("1", "Max", null, "dog", "Golden Retriever"),
            Pet("2", "Luna", null, "cat", "Siamés"),
            Pet("3", "Rocky", null, "dog", "Bulldog"),
            Pet("4", "Mia", null, "cat", "Persa")
        )

        petsAdapter = PetsAdapter(
            pets = myPets,
            onAddClick = {
                Toast.makeText(this, "Agregar nueva mascota", Toast.LENGTH_SHORT).show()
                // Aquí puedes abrir una actividad para agregar mascota
            },
            onPetClick = { pet ->
<<<<<<< HEAD
                Toast.makeText(this, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
=======
                Toast.makeText(this, "Seleccionaste a ${pet.name}", Toast.LENGTH_SHORT).show()
                // Aquí puedes abrir el detalle de la mascota
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
            }
        )

        myPetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@DashboardActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = petsAdapter
<<<<<<< HEAD
=======
            // Agregar espaciado entre items si tienes el decorator
            // addItemDecoration(HorizontalSpaceItemDecoration(16))
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
        }
    }

    private fun setupAdoptionRecyclerView() {
        val adoptionPets = listOf(
            AdoptionPet("1", "Bobby", "Golden Retriever", null, 2),
            AdoptionPet("2", "Mimi", "Persa", null, 1),
            AdoptionPet("3", "Rex", "Pastor Alemán", null, 3),
            AdoptionPet("4", "Bella", "Labrador", null, 1)
        )

        adoptionAdapter = AdoptionAdapter(
            pets = adoptionPets,
            onAdoptClick = { pet ->
<<<<<<< HEAD
                Toast.makeText(this, "¿Adoptar a ${pet.name}?", Toast.LENGTH_SHORT).show()
=======
                Toast.makeText(this, "¿Quieres adoptar a ${pet.name}?", Toast.LENGTH_SHORT).show()
                // Aquí puedes abrir el proceso de adopción
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
            }
        )

        adoptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 2) // 2 columnas
            adapter = adoptionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
<<<<<<< HEAD
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
=======
        // Notificaciones
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Ver notificaciones", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la actividad de notificaciones
            notificationDot.visibility = View.GONE // Ocultar el punto al abrir
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
        }

        // Perfil
        profileImageCard.setOnClickListener {
            Toast.makeText(this, "Ver perfil", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la actividad de perfil
        }

<<<<<<< HEAD
=======
        // Filtro de búsqueda
        filterButton.setOnClickListener {
            Toast.makeText(this, "Abrir filtros", Toast.LENGTH_SHORT).show()
            // Aquí puedes mostrar un bottom sheet con filtros
        }

        // Búsqueda
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Buscando: $query", Toast.LENGTH_SHORT).show()
                // Aquí puedes implementar la búsqueda
            }
            true
        }

        // Quick action: Encontrar pareja
        findPartnerCard.setOnClickListener {
            Toast.makeText(this, "Buscar pareja para tu mascota", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la actividad de búsqueda de pareja
        }

        // Quick action: Adoptar mascota
        adoptPetCard.setOnClickListener {
            Toast.makeText(this, "Ver todas las mascotas en adopción", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la lista completa de adopción
        }

        // Ver todas mis mascotas
        viewAllMyPets.setOnClickListener {
            Toast.makeText(this, "Ver todas mis mascotas", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la lista completa de tus mascotas
        }

        // Ver todas las mascotas en adopción
        viewAllAdoptionPets.setOnClickListener {
            Toast.makeText(this, "Ver todas las mascotas en adopción", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la lista completa de adopción
        }

        // Servicios
        veterinaryCard.setOnClickListener {
            Toast.makeText(this, "Servicios veterinarios", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la lista de veterinarias
        }

        groomingCard.setOnClickListener {
            Toast.makeText(this, "Servicios de peluquería", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la lista de peluquerías
        }

        storeCard.setOnClickListener {
            Toast.makeText(this, "Tienda de mascotas", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la tienda
        }

        // Banner promocional
        promoBanner.setOnClickListener {
            Toast.makeText(this, "Ver detalles de la promoción", Toast.LENGTH_SHORT).show()
            // Aquí puedes mostrar más información de la oferta
        }
    }
<<<<<<< HEAD
=======

    // Función pública para agregar una nueva mascota
    fun addNewPet(pet: Pet) {
        petsAdapter.addPet(pet)
    }
>>>>>>> abffdabc6e00b305a46e34529c932e26add032cf
}

// ============================================
// MODELOS DE DATOS
// ============================================

