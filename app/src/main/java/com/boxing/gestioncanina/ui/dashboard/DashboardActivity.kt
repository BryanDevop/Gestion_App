package com.boxing.gestioncanina.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.ui.settings.ProfileFragment
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {

    private lateinit var petsRecyclerView: RecyclerView
    private lateinit var adoptionRecyclerView: RecyclerView
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var adoptionAdapter: AdoptionAdapter

    private lateinit var menuButton: ImageView
    private lateinit var notificationButton: FrameLayout
    private lateinit var notificationBadge: TextView
    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var searchInput: EditText
    private lateinit var matingButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Inicializar vistas
        initViews()

        // Configurar RecyclerViews
        setupPetsRecyclerView()
        setupAdoptionRecyclerView()

        // Configurar listeners
        setupClickListeners()
    }

    private fun initViews() {
        // Toolbar
        menuButton = findViewById(R.id.menuButton)
        notificationButton = findViewById(R.id.notificationButton)
        notificationBadge = findViewById(R.id.notificationBadge)
        profileImage = findViewById(R.id.profileImage)
        userName = findViewById(R.id.userName)

        // Búsqueda
        searchInput = findViewById(R.id.searchInput)

        // RecyclerViews
        petsRecyclerView = findViewById(R.id.petsRecyclerView)
        adoptionRecyclerView = findViewById(R.id.adoptionRecyclerView)

        // Botones
        matingButton = findViewById(R.id.matingButton)

        // Configurar nombre de usuario (puedes obtenerlo de SharedPreferences o de donde lo tengas)
        userName.text = "Bryan Greimi"

        // Configurar badge de notificaciones
        notificationBadge.text = "3" // Número de notificaciones
    }

    private fun setupPetsRecyclerView() {
        // Datos de ejemplo - Reemplaza con tus datos reales
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
                // TODO: Abrir Activity o Dialog para agregar mascota
            },
            onPetClick = { pet ->
                Toast.makeText(this, "Seleccionaste a ${pet.name}", Toast.LENGTH_SHORT).show()
                // TODO: Abrir detalle de la mascota
            }
        )

        petsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@DashboardActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = petsAdapter
            // Agregar espaciado entre items
            addItemDecoration(HorizontalSpaceItemDecoration(16))
        }
    }

    private fun setupAdoptionRecyclerView() {
        // Datos de ejemplo - Reemplaza con tus datos reales
        val adoptionPets = listOf(
            AdoptionPet("1", "Bobby", "Golden Retriever", null, 2),
            AdoptionPet("2", "Mimi", "Persa", null, 1),
            AdoptionPet("3", "Rex", "Pastor Alemán", null, 3),
            AdoptionPet("4", "Bella", "Labrador", null, 1),
            AdoptionPet("5", "Tom", "Siamés", null, 2),
            AdoptionPet("6", "Coco", "Beagle", null, 1),
            AdoptionPet("7", "Luna", "Husky", null, 2),
            AdoptionPet("8", "Max", "Poodle", null, 3),
            AdoptionPet("9", "Nina", "Chihuahua", null, 1)
        )

        adoptionAdapter = AdoptionAdapter(
            pets = adoptionPets,
            onAdoptClick = { pet ->
                Toast.makeText(
                    this,
                    "¿Quieres adoptar a ${pet.name}?",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Abrir detalle o proceso de adopción
            }
        )

        adoptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 3)
            adapter = adoptionAdapter
            isNestedScrollingEnabled = false // Importante para que funcione dentro del NestedScrollView
        }
    }

    private fun setupClickListeners() {
        // Menú hamburguesa
        menuButton.setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
            Toast.makeText(this, "Abrir menú lateral", Toast.LENGTH_SHORT).show()
            // TODO: Abrir Navigation Drawer o menú lateral
        }

        // Notificaciones
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Ver notificaciones", Toast.LENGTH_SHORT).show()
            // TODO: Abrir pantalla de notificaciones
        }

        // Perfil
        profileImage.setOnClickListener {
            Toast.makeText(this, "Ver perfil", Toast.LENGTH_SHORT).show()
            // TODO: Abrir pantalla de perfil
        }

        // Botón de encontrar pareja
        matingButton.setOnClickListener {
            Toast.makeText(this, "Buscar pareja para tu mascota", Toast.LENGTH_SHORT).show()
            // TODO: Abrir pantalla de búsqueda de pareja
        }

        // Búsqueda
        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Buscando: $query", Toast.LENGTH_SHORT).show()
                // TODO: Implementar búsqueda
            }
            true
        }
    }

    // Función auxiliar para agregar mascotas dinámicamente
    fun addNewPet(pet: Pet) {
        petsAdapter.addPet(pet)
    }
}