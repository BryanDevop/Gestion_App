package com.boxing.gestioncanina.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.boxing.gestioncanina.R
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Biography_Animal : AppCompatActivity() {

    companion object {
        private const val TAG = "Biography_Animal"
    }

    // Views
    private lateinit var imgPet: ImageView
    private lateinit var tvPetName: TextView
    private lateinit var tvPetBreed: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var fabBack: FloatingActionButton
    private lateinit var fabFavorite: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biography_animal)

        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🐕 Iniciando Biography_Animal")
        Log.d(TAG, "════════════════════════════════════════")

        initViews()
        loadPetData()
        setupClickListeners()
    }

    private fun initViews() {
        Log.d(TAG, "🔧 Inicializando views...")

        imgPet = findViewById(R.id.imgPet)
        tvPetName = findViewById(R.id.tvPetName)
        tvPetBreed = findViewById(R.id.tvPetBreed)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvWeight = findViewById(R.id.tvWeight)
        tvLocation = findViewById(R.id.tvLocation)
        tvDescription = findViewById(R.id.tvDescription)
        fabBack = findViewById(R.id.fabBack)
        fabFavorite = findViewById(R.id.fabFavorite)

        Log.d(TAG, "✅ Views inicializadas correctamente")
    }

    private fun loadPetData() {
        Log.d(TAG, "📦 Cargando datos de la mascota desde Intent...")

        // Recibir datos del Intent
        val petId = intent.getStringExtra("PET_ID") ?: ""
        val petName = intent.getStringExtra("PET_NAME") ?: "Sin nombre"
        val petBreed = intent.getStringExtra("PET_BREED") ?: "Raza desconocida"
        val petAge = intent.getIntExtra("PET_AGE", 0)
        val petImageUrl = intent.getStringExtra("PET_IMAGE_URL") ?: ""
        val petDescription = intent.getStringExtra("PET_DESCRIPTION")
            ?: "Mascota cariñosa en busca de un hogar lleno de amor."

        // ⬇️⬇️⬇️ RECIBIR NUEVOS DATOS ⬇️⬇️⬇️
        val petGender = intent.getStringExtra("PET_GENDER") ?: "No especificado"
        val petWeight = intent.getDoubleExtra("PET_WEIGHT", 0.0)
        val petLocation = intent.getStringExtra("PET_LOCATION")
            ?: "Santo Domingo, República Dominicana"
        val shelterName = intent.getStringExtra("SHELTER_NAME")
            ?: "Refugio Patitas Felices"
        val shelterPhone = intent.getStringExtra("SHELTER_PHONE") ?: ""

        Log.d(TAG, "📝 Datos recibidos:")
        Log.d(TAG, "   ID: $petId")
        Log.d(TAG, "   Nombre: $petName")
        Log.d(TAG, "   Raza: $petBreed")
        Log.d(TAG, "   Edad: $petAge")
        Log.d(TAG, "   Género: $petGender")
        Log.d(TAG, "   Peso: $petWeight kg")
        Log.d(TAG, "   Ubicación: $petLocation")
        Log.d(TAG, "   Refugio: $shelterName")
        Log.d(TAG, "   Teléfono: $shelterPhone")

        // Mostrar datos básicos
        tvPetName.text = petName
        tvPetBreed.text = petBreed
        tvAge.text = if (petAge == 1) "1 año" else "$petAge años"
        tvDescription.text = petDescription

        // ⬇️⬇️⬇️ MOSTRAR NUEVOS DATOS REALES ⬇️⬇️⬇️
        tvGender.text = petGender
        tvWeight.text = if (petWeight > 0) "${petWeight} kg" else "-- kg"
        tvLocation.text = petLocation

        // Mostrar nombre del refugio
        val shelterNameView: TextView = findViewById(R.id.tvShelterName)
        shelterNameView.text = shelterName

        // Cargar imagen con Glide
        if (petImageUrl.isNotEmpty()) {
            Log.d(TAG, "📷 Cargando imagen...")
            Glide.with(this)
                .load(petImageUrl)
                .placeholder(R.drawable.ic_pet_placeholder)
                .error(R.drawable.ic_pet_placeholder)
                .centerCrop()
                .into(imgPet)
            Log.d(TAG, "✅ Imagen cargada")
        } else {
            Log.w(TAG, "⚠️ No hay URL de imagen, usando placeholder")
            imgPet.setImageResource(R.drawable.ic_pet_placeholder)
        }

        Log.d(TAG, "✅ Datos de mascota cargados exitosamente")
    }

    private fun setupClickListeners() {
        Log.d(TAG, "🔧 Configurando click listeners...")

        // Botón de regresar
        fabBack.setOnClickListener {
            Log.d(TAG, "👆 Click en botón regresar")
            finish() // Cierra esta Activity y regresa a la anterior
        }

        // Botón de favorito (placeholder por ahora)
        fabFavorite.setOnClickListener {
            Log.d(TAG, "👆 Click en botón favorito")
            // TODO: Implementar funcionalidad de favoritos
        }

        Log.d(TAG, "✅ Click listeners configurados")
    }
}