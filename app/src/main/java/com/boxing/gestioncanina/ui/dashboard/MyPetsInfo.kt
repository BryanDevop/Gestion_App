package com.boxing.gestioncanina.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.network.Supabase
import com.boxing.gestioncanina.data.model.Pet
import com.boxing.gestioncanina.models.PetSupabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPetsInfo : AppCompatActivity() {

    companion object {
        private const val TAG = "MyPetsInfo"
    }

    // Views
    private lateinit var rvPets: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var fabAddPet: ExtendedFloatingActionButton
    private lateinit var emptyStateLayout: LinearLayout

    // Adapter
    private lateinit var myPetsAdapter: MyPetsAdapter
    private var allPets: List<Pet> = emptyList()
    private var filteredPets: List<Pet> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets_info)

        // Configurar edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupSearchBar()
        setupFab()
        loadPetsFromSupabase()
    }

    private fun initViews() {
        rvPets = findViewById(R.id.rvPets)
        etSearch = findViewById(R.id.etSearch)
        fabAddPet = findViewById(R.id.fabAddPet)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
    }

    private fun setupRecyclerView() {
        myPetsAdapter = MyPetsAdapter(
            pets = emptyList(),
            onPetClick = { pet ->
                showPetDetailsDialog(pet)
            }
        )

        rvPets.apply {
            layoutManager = GridLayoutManager(this@MyPetsInfo, 2)
            adapter = myPetsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchBar() {
        etSearch.addTextChangedListener { text ->
            val query = text.toString()
            filterPets(query)
        }
    }

    private fun setupFab() {
        fabAddPet.setOnClickListener {
            navigateToAddPet()
        }
    }

    private fun filterPets(query: String) {
        filteredPets = if (query.isEmpty()) {
            allPets
        } else {
            allPets.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.breed.contains(query, ignoreCase = true)
            }
        }

        myPetsAdapter.updatePets(filteredPets)
        updateEmptyState()
    }

    private fun loadPetsFromSupabase() {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "📡 CARGANDO MIS MASCOTAS DESDE SUPABASE")
        Log.d(TAG, "════════════════════════════════════════")

        val user = Supabase.client.auth.currentUserOrNull()

        if (user == null) {
            Log.e(TAG, "❌ Usuario no autenticado - No se pueden cargar mascotas")
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            showEmptyState(true)
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
                        imageUrl = it.image_url
                    )
                }

                withContext(Dispatchers.Main) {
                    if (pets.isEmpty()) {
                        Log.w(TAG, "⚠️ No hay mascotas registradas para este usuario")
                    } else {
                        Log.d(TAG, "✅ Se cargaron ${pets.size} mascotas correctamente")
                    }

                    allPets = pets
                    filteredPets = pets
                    myPetsAdapter.updatePets(pets)
                    updateEmptyState()

                    Log.d(TAG, "🔄 UI actualizada con ${pets.size} mascotas")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR al cargar mascotas", e)
                Log.e(TAG, "💥 Tipo de error: ${e.javaClass.simpleName}")
                Log.e(TAG, "💬 Mensaje: ${e.message}")

                withContext(Dispatchers.Main) {
                    showEmptyState(true)
                    Toast.makeText(
                        this@MyPetsInfo,
                        "Error al cargar mascotas: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun updateEmptyState() {
        val searchQuery = etSearch.text.toString()
        val shouldShowEmpty = allPets.isEmpty() && searchQuery.isEmpty()
        showEmptyState(shouldShowEmpty)
    }

    private fun showEmptyState(show: Boolean) {
        if (show) {
            emptyStateLayout.visibility = View.VISIBLE
            rvPets.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            rvPets.visibility = View.VISIBLE
        }
    }

    /**
     * Muestra un diálogo con los detalles de la mascota y opción de eliminar
     */
    private fun showPetDetailsDialog(pet: Pet) {
        Log.d(TAG, "🐕 Mostrando detalles de mascota: ${pet.name}")

        // Inflar el layout personalizado
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pet_details, null)

        // Crear el diálogo
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Hacer el fondo del diálogo transparente para mostrar las esquinas redondeadas
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Referencias a las vistas del diálogo
        val ivDialogPetImage: ImageView = dialogView.findViewById(R.id.ivDialogPetImage)
        val tvDialogPetName: TextView = dialogView.findViewById(R.id.tvDialogPetName)
        val tvDialogPetBreed: TextView = dialogView.findViewById(R.id.tvDialogPetBreed)
        val tvDialogPetAge: TextView = dialogView.findViewById(R.id.tvDialogPetAge)
        val btnDeletePet: MaterialButton = dialogView.findViewById(R.id.btnDeletePet)
        val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)

        // Configurar los datos de la mascota
        tvDialogPetName.text = pet.name
        tvDialogPetBreed.text = pet.breed
        tvDialogPetAge.text = if (pet.age == 1) "${pet.age} año" else "${pet.age} años"

        // Cargar imagen con Glide
        if (!pet.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(pet.imageUrl)
                .placeholder(R.drawable.ic_pet_placeholder)
                .error(R.drawable.ic_pet_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivDialogPetImage)
        } else {
            ivDialogPetImage.setImageResource(R.drawable.ic_pet_placeholder)
        }

        // Botón Cancelar
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Botón Eliminar
        btnDeletePet.setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmationDialog(pet)
        }

        dialog.show()
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar
     */
    private fun showDeleteConfirmationDialog(pet: Pet) {
        AlertDialog.Builder(this)
            .setTitle("¿Eliminar mascota?")
            .setMessage("¿Estás seguro de que deseas eliminar a ${pet.name}? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                deletePetFromSupabase(pet)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    /**
     * Elimina una mascota de Supabase
     */
    private fun deletePetFromSupabase(pet: Pet) {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🗑️ ELIMINANDO MASCOTA DE SUPABASE")
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🆔 Pet ID: ${pet.id}")
        Log.d(TAG, "🐕 Nombre: ${pet.name}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Eliminar de Supabase
                Supabase.client
                    .from("pets")
                    .delete {
                        filter {
                            eq("id", pet.id.toString())
                        }
                    }

                Log.d(TAG, "✅ Mascota eliminada exitosamente de Supabase")

                withContext(Dispatchers.Main) {
                    // Actualizar la lista local
                    allPets = allPets.filter { it.id != pet.id }
                    filteredPets = filteredPets.filter { it.id != pet.id }

                    // Actualizar el adapter
                    myPetsAdapter.updatePets(filteredPets)
                    updateEmptyState()

                    Toast.makeText(
                        this@MyPetsInfo,
                        "🗑️ ${pet.name} ha sido eliminado",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(TAG, "🔄 UI actualizada - Mascota eliminada de la lista")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR al eliminar mascota", e)
                Log.e(TAG, "💥 Tipo de error: ${e.javaClass.simpleName}")
                Log.e(TAG, "💬 Mensaje: ${e.message}")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MyPetsInfo,
                        "Error al eliminar la mascota: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun navigateToAddPet() {
        Log.d(TAG, "➕ Navegando a agregar mascota")

        // TODO: Implementar navegación a tu pantalla de agregar mascota
        // val intent = Intent(this, AddPetActivity::class.java)
        // startActivity(intent)

        Toast.makeText(this, "Función para agregar mascota próximamente", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Recargar las mascotas cuando se regrese a esta pantalla
        loadPetsFromSupabase()
    }
}