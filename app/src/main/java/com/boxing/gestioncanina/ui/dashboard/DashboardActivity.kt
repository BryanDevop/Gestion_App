package com.boxing.gestioncanina.ui.dashboard

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
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {

    private lateinit var petsRecyclerView: RecyclerView
    private lateinit var adoptionRecyclerView: RecyclerView
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var adoptionAdapter: AdoptionAdapter

    private lateinit var notificationButton: FrameLayout
    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var searchInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

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
    }

    private fun setupPetsRecyclerView() {
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
            },
            onPetClick = { pet ->
                Toast.makeText(this, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
            }
        )

        petsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@DashboardActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = petsAdapter
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
                Toast.makeText(this, "¿Adoptar a ${pet.name}?", Toast.LENGTH_SHORT).show()
            }
        )

        adoptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 3)
            adapter = adoptionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        notificationButton.setOnClickListener {
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
        }

        profileImage.setOnClickListener {
            Toast.makeText(this, "Ver perfil", Toast.LENGTH_SHORT).show()
        }

        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Buscando: $query", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
}
