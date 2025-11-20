package com.boxing.gestioncanina.ui.dashboard

import android.content.Intent
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
import com.boxing.gestioncanina.ui.auth.LoginActivity
import com.boxing.gestioncanina.ui.settings.ProfileActivity
import com.boxing.gestioncanina.ui.settings.ProfileFragment

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DashboardActivity", "onCreate iniciado")

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
                Toast.makeText(this, "Seleccionaste a ${pet.name}", Toast.LENGTH_SHORT).show()
                // Aquí puedes abrir el detalle de la mascota
            }
        )

        myPetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@DashboardActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = petsAdapter
            // Agregar espaciado entre items si tienes el decorator
            // addItemDecoration(HorizontalSpaceItemDecoration(16))
        }
    }

    private fun setupAdoptionRecyclerView() {
        val adoptionPets = listOf(
            AdoptionPet("1", "Bobby", "Golden Retriever", "https://www.respetmascotas.com/_Assets/img/181129-Imagen-AlimentacionMascotas.jpg", 2),
            AdoptionPet("2", "Mimi", "Persa", "https://ichef.bbci.co.uk/ace/ws/640/amz/worldservice/live/assets/images/2015/06/12/150612173542_mascota_promo_624x351_thinkstock.jpg.webp", 1),
            AdoptionPet("3", "Rex", "Pastor Alemán", "https://www.ladridosybigotes.com/content/images/2024/10/2024-08-13-animal-hoarding-disorder.webp", 3),
            AdoptionPet("4", "Bella", "Labrador", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRSKa_yQEs7KxcLfVrFyEhKoHuX6EjIVzS3XA&s", 1),
            AdoptionPet("5", "Tom", "Siamés", "https://cdn.pixabay.com/photo/2018/10/01/09/21/pets-3715733_640.jpg", 2),
            AdoptionPet("6", "Coco", "Beagle", "https://media.istockphoto.com/id/1445196818/es/foto/grupo-de-lindas-mascotas-sobre-fondo-blanco-dise%C3%B1o-de-banner.jpg?s=612x612&w=0&k=20&c=JTUk_9yiSEj1ahD4K68d13oiTsp1ks9PmCxK1bPzxKI=", 1),
            AdoptionPet("7", "Luna", "Husky", "https://pharmadiet.com/wp-content/uploads/2021/05/240411-post-blog-piel-sana-mascotas-1.png", 2),
            AdoptionPet("8", "Max", "Poodle", "https://es.statefarm.com/content/dam/sf-library/en-us/secure/legacy/simple-insights/pet-parasites.jpg", 3),
            AdoptionPet("9", "Nina", "Chihuahua", "https://www.triada.com.pe/noticias/wp-content/uploads/2022/05/MascotasHuesito-1210x700.jpg", 1),
                    AdoptionPet("10", "Rocky", "Bulldog", "https://www.casasnuevasaqui.com/guia/wp-content/uploads/2021/06/pomeranian.jpg.webp", 2),
            AdoptionPet("11", "Kira", "Doberman", "https://seguros.elcorteingles.es/content/dam/eci-seguros/es/blog/blog-julio-2023/incluir-mascota-seguro-hogar.jpg", 1),
            AdoptionPet("12", "Simba", "Maine Coon", "https://images.ctfassets.net/denf86kkcx7r/4pwGRbC3uAhaMf6w59Cu4G/41d14d2a76370f521ff6ebff41203f2b/articledangerinspring-86?fm=webp&w=612", 3),
            AdoptionPet("13", "Toby", "Shih Tzu", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s", 2),
            AdoptionPet("14", "Molly", "Border Collie", "https://a.files.bbci.co.uk/worldservice/live/assets/images/2015/06/12/150612172633_mascotas2.png", 1),
            AdoptionPet("15", "Chispa", "Dálmata", "https://fotografias.antena3.com/clipping/cmsimages01/2021/08/03/FBA37CA8-72F1-4DE1-A359-9A0EBDED7AAB/98.jpg?crop=1920,1080,x0,y99&width=1900&height=1069&optimize=low&format=webply", 3),
            AdoptionPet("16", "Fiona", "Pastor Belga", "https://cdn.forbes.com.mx/2023/03/perros-mascota.webp", 2),
            AdoptionPet("17", "Zeus", "Rottweiler", "https://www.centroeleia.edu.mx/blog/wp-content/uploads/2021/03/mascotas_slider.jpg", 1),
            AdoptionPet("18", "Pelusa", "Angora", "https://static.wixstatic.com/media/4c919a_1f8f08e6cb5f4a62945a3def4b9d70c7~mv2.jpg/v1/fill/w_670,h_502,al_c,q_80,usm_0.66_1.00_0.01,enc_avif,quality_auto/4c919a_1f8f08e6cb5f4a62945a3def4b9d70c7~mv2.jpg", 2),
            AdoptionPet("19", "Duke", "Pitbull", "https://traveler.marriott.com/es/wp-content/uploads/sites/2/2022/03/GI-764782369-Couple-Beach-Pet-1920x1080.jpg", 3),

        )

        adoptionAdapter = AdoptionAdapter(
            pets = adoptionPets,
            onAdoptClick = { pet ->
                val intent = Intent(this, Biography_Animal::class.java)
                startActivity(intent)
                Toast.makeText(this, "¿Quieres adoptar a ${pet.name}?", Toast.LENGTH_SHORT).show()
                // Aquí puedes abrir el proceso de adopción
            }
        )

        adoptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 2) // 2 columnas
            adapter = adoptionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        // Notificaciones
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Ver notificaciones", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la actividad de notificaciones
            notificationDot.visibility = View.GONE // Ocultar el punto al abrir
        }

        // Perfil
        profileImageCard.setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
            Toast.makeText(this, "Ver perfil", Toast.LENGTH_SHORT).show()
            // Aquí puedes abrir la actividad de perfil
        }

        // Filtro de búsqueda
        filterButton.setOnClickListener {
            Toast.makeText(this, "Abrir filtros", Toast.LENGTH_SHORT).show()
            // Aquí puedes mostrar un bottom sheet con filtros
        }

        // Búsqueda
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

    // Función pública para agregar una nueva mascota
    fun addNewPet(pet: Pet) {
        petsAdapter.addPet(pet)
    }
}

// ============================================
// MODELOS DE DATOS
// ============================================

