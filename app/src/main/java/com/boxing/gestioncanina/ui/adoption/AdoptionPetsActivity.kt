package com.boxing.gestioncanina.ui.adoption

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import io.github.jan.supabase.gotrue.auth
import com.boxing.gestioncanina.ui.dashboard.InsertPet
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.ImageView
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.models.AdoptionPetUI
import com.boxing.gestioncanina.data.network.Supabase
import com.boxing.gestioncanina.data.models.AdoptionPet
import com.boxing.gestioncanina.ui.dashboard.AdoptionAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdoptionPetsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AdoptionPetsActivity"
    }

    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchBar: TextInputEditText
    private lateinit var chipGroupAge: ChipGroup
    private lateinit var chipPuppy: Chip
    private lateinit var chipAdult: Chip
    private lateinit var chipSenior: Chip
    private lateinit var btnClearFilters: MaterialButton
    private lateinit var tvResultCount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerViewPets: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var btnRetry: MaterialButton

    // Adapter
    private lateinit var adoptionAdapter: AdoptionAdapter

    // Data
    private var allPets: List<AdoptionPet> = emptyList()
    private var filteredPets: List<AdoptionPet> = emptyList()

    // Filtros
    private var currentSearchQuery: String = ""
    private var currentAgeFilter: AgeFilter = AgeFilter.ALL

    // Control para evitar múltiples adopciones simultáneas
    private var isProcessingAdoption = false

    enum class AgeFilter {
        ALL,
        PUPPY,    // 0-2 años
        ADULT,    // 2-7 años
        SENIOR    // 7+ años
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🚀 onCreate: Iniciando AdoptionPetsActivity")
        Log.d(TAG, "════════════════════════════════════════")

        setContentView(R.layout.activity_adoption_pets_fragment)

        try {
            initViews()
            Log.d(TAG, "✅ Views inicializadas correctamente")

            setupToolbar()
            Log.d(TAG, "✅ Toolbar configurado")

            setupRecyclerView()
            Log.d(TAG, "✅ RecyclerView configurado")

            setupSearchBar()
            Log.d(TAG, "✅ Barra de búsqueda configurada")

            setupFilters()
            Log.d(TAG, "✅ Filtros configurados")

            setupSwipeRefresh()
            Log.d(TAG, "✅ SwipeRefresh configurado")

            setupClickListeners()
            Log.d(TAG, "✅ Click listeners configurados")

            loadAdoptionPets()

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR CRÍTICO en onCreate: ${e.message}", e)
            Toast.makeText(this, "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        Log.d(TAG, "🔧 Inicializando views...")

        toolbar = findViewById(R.id.toolbar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        searchBar = findViewById(R.id.searchBar)
        chipGroupAge = findViewById(R.id.chipGroupAge)
        chipPuppy = findViewById(R.id.chipPuppy)
        chipAdult = findViewById(R.id.chipAdult)
        chipSenior = findViewById(R.id.chipSenior)
        btnClearFilters = findViewById(R.id.btnClearFilters)
        tvResultCount = findViewById(R.id.tvResultCount)
        progressBar = findViewById(R.id.progressBar)
        recyclerViewPets = findViewById(R.id.recyclerViewPets)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        errorLayout = findViewById(R.id.errorLayout)
        errorText = findViewById(R.id.errorText)
        btnRetry = findViewById(R.id.btnRetry)

        Log.d(TAG, "✅ Todos los views encontrados")
    }

    private fun setupToolbar() {
        Log.d(TAG, "🔧 Configurando toolbar...")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            Log.d(TAG, "👆 Click en botón volver")
            finish()
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "🔧 Configurando RecyclerView...")

        adoptionAdapter = AdoptionAdapter(
            pets = mutableListOf(),
            onAdoptClick = { pet ->
                Log.d(TAG, "❤️ Click en adoptar: ${pet.name}")
                showAdoptionModal(pet)
            }
        )

        recyclerViewPets.apply {
            layoutManager = GridLayoutManager(this@AdoptionPetsActivity, 2)
            adapter = adoptionAdapter
        }

        Log.d(TAG, "✅ RecyclerView configurado (Grid 2 columnas)")
    }

    private fun showAdoptionModal(pet: AdoptionPet) {
        Log.d(TAG, "🐕 Mostrando modal de adopción para: ${pet.name}")

        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.modal_adopt_pet, null)

        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val petImage: ImageView = view.findViewById(R.id.petImage)
        val petName: TextView = view.findViewById(R.id.petName)
        val petBreed: TextView = view.findViewById(R.id.petBreed)
        val petAge: TextView = view.findViewById(R.id.petAge)
        val confirmButton: CardView = view.findViewById(R.id.confirmAdoptButton)
        val buttonText: TextView = view.findViewById(R.id.buttonText)

        petName.text = pet.name
        petBreed.text = pet.breed
        petAge.text = "${pet.age} años"

        if (pet.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(pet.imageUrl)
                .centerCrop()
                .into(petImage)
        }

        closeButton.setOnClickListener {
            Log.d(TAG, "❌ Modal de adopción cerrado")
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            Log.d(TAG, "✅ Confirmando adopción de: ${pet.name}")
            confirmAdoption(pet, dialog, buttonText, confirmButton)
        }

        dialog.setContentView(view)
        dialog.show()
        Log.d(TAG, "✅ Modal de adopción mostrado")
    }

    private fun confirmAdoption(
        pet: AdoptionPet,
        dialog: BottomSheetDialog,
        buttonText: TextView,
        confirmButton: CardView
    ) {
        if (isProcessingAdoption) {
            Log.w(TAG, "⚠️ Ya hay una adopción en proceso, ignorando click")
            return
        }

        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "💝 PROCESANDO ADOPCIÓN")
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "🐕 Mascota: ${pet.name}")
        Log.d(TAG, "🆔 ID: ${pet.id}")

        isProcessingAdoption = true
        buttonText.text = "Procesando..."
        confirmButton.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ✅ OBTENER USUARIO DENTRO DE LA COROUTINE
                val user = Supabase.client.auth.currentUserOrNull()

                if (user == null) {
                    Log.e(TAG, "❌ Usuario no autenticado")
                    withContext(Dispatchers.Main) {
                        isProcessingAdoption = false
                        buttonText.text = "Confirmar Adopción"
                        confirmButton.isEnabled = true
                        Toast.makeText(
                            this@AdoptionPetsActivity,
                            "Debes iniciar sesión para adoptar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                Log.d(TAG, "🆔 User ID: ${user.id}")

                // PASO 1: Verificar si ya fue adoptada
                Log.d(TAG, "📝 PASO 0: Verificando estado actual...")

                val currentPet = Supabase.client
                    .from("adoption_pets")
                    .select(Columns.list("is_adopted")) {
                        filter {
                            eq("id", pet.id)
                        }
                    }
                    .decodeSingleOrNull<Map<String, Boolean>>()

                if (currentPet?.get("is_adopted") == true) {
                    Log.w(TAG, "⚠️ La mascota ya fue adoptada")
                    withContext(Dispatchers.Main) {
                        isProcessingAdoption = false
                        buttonText.text = "Confirmar Adopción"
                        confirmButton.isEnabled = true
                        dialog.dismiss()

                        Toast.makeText(
                            this@AdoptionPetsActivity,
                            "Lo sentimos, ${pet.name} ya fue adoptado/a",
                            Toast.LENGTH_LONG
                        ).show()

                        loadAdoptionPets()
                    }
                    return@launch
                }

                // PASO 2: Actualizar is_adopted = true
                Log.d(TAG, "📝 PASO 1: Actualizando estado is_adopted = true...")

                Supabase.client
                    .from("adoption_pets")
                    .update(
                        mapOf("is_adopted" to true)
                    ) {
                        filter {
                            eq("id", pet.id)
                        }
                    }

                Log.d(TAG, "✅ Estado actualizado en adoption_pets")

                // PASO 3: Agregar a tabla pets
                Log.d(TAG, "📝 PASO 2: Agregando a tabla 'pets'...")

                val newPet = InsertPet(
                    user_id = user.id.toString(),
                    name = pet.name,
                    breed = pet.breed,
                    age = pet.age,
                    image_url = pet.imageUrl.ifEmpty { null }
                )

                Log.d(TAG, "📦 Datos a insertar: $newPet")

                Supabase.client
                    .from("pets")
                    .insert(newPet)

                Log.d(TAG, "✅ Mascota agregada a 'pets'")
                Log.d(TAG, "════════════════════════════════════════")
                Log.d(TAG, "🎉 ADOPCIÓN COMPLETADA")
                Log.d(TAG, "════════════════════════════════════════")

                withContext(Dispatchers.Main) {
                    isProcessingAdoption = false
                    dialog.dismiss()

                    Toast.makeText(
                        this@AdoptionPetsActivity,
                        "¡Felicidades! Has adoptado a ${pet.name} 🎉",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.d(TAG, "🔄 Recargando lista...")
                    loadAdoptionPets()
                }

            } catch (e: Exception) {
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "❌ ERROR EN ADOPCIÓN")
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "💥 Tipo: ${e.javaClass.simpleName}")
                Log.e(TAG, "💬 Mensaje: ${e.message}")
                Log.e(TAG, "📚 Stack trace completo:", e)

                withContext(Dispatchers.Main) {
                    isProcessingAdoption = false
                    buttonText.text = "Confirmar Adopción"
                    confirmButton.isEnabled = true

                    val errorMessage = when {
                        e.message?.contains("duplicate", ignoreCase = true) == true ->
                            "Ya has adoptado esta mascota"
                        e.message?.contains("network", ignoreCase = true) == true ->
                            "Error de conexión"
                        e.message?.contains("auth", ignoreCase = true) == true ->
                            "Error de autenticación"
                        else -> "Error: ${e.message}"
                    }

                    Toast.makeText(
                        this@AdoptionPetsActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupSearchBar() {
        Log.d(TAG, "🔧 Configurando barra de búsqueda...")

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""

                if (query != currentSearchQuery) {
                    currentSearchQuery = query
                    Log.d(TAG, "🔍 Búsqueda: '$query'")
                    applyFilters()
                }
            }
        })

        Log.d(TAG, "✅ Barra de búsqueda configurada")
    }

    private fun setupFilters() {
        Log.d(TAG, "🔧 Configurando filtros de edad...")

        chipGroupAge.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.contains(R.id.chipPuppy) -> {
                    currentAgeFilter = AgeFilter.PUPPY
                    Log.d(TAG, "🐶 Filtro: Cachorro (0-2 años)")
                }
                checkedIds.contains(R.id.chipAdult) -> {
                    currentAgeFilter = AgeFilter.ADULT
                    Log.d(TAG, "🐕 Filtro: Adulto (2-7 años)")
                }
                checkedIds.contains(R.id.chipSenior) -> {
                    currentAgeFilter = AgeFilter.SENIOR
                    Log.d(TAG, "🦮 Filtro: Senior (7+ años)")
                }
                else -> {
                    currentAgeFilter = AgeFilter.ALL
                    Log.d(TAG, "🌟 Filtro: Todas las edades")
                }
            }
            applyFilters()
        }

        Log.d(TAG, "✅ Filtros configurados")
    }

    private fun setupSwipeRefresh() {
        Log.d(TAG, "🔧 Configurando SwipeRefresh...")

        swipeRefresh.setOnRefreshListener {
            Log.d(TAG, "🔄 Pull to refresh activado")
            loadAdoptionPets()
        }

        Log.d(TAG, "✅ SwipeRefresh configurado")
    }

    private fun setupClickListeners() {
        Log.d(TAG, "🔧 Configurando click listeners...")

        btnClearFilters.setOnClickListener {
            Log.d(TAG, "🧹 Limpiando filtros")
            clearFilters()
        }

        btnRetry.setOnClickListener {
            Log.d(TAG, "🔄 Reintentando carga")
            loadAdoptionPets()
        }

        Log.d(TAG, "✅ Click listeners configurados")
    }

    private fun loadAdoptionPets() {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "📡 CARGANDO MASCOTAS EN ADOPCIÓN")
        Log.d(TAG, "════════════════════════════════════════")

        showLoading()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔍 Consultando tabla 'adoption_pets'...")

                val response = Supabase.client
                    .from("adoption_pets")
                    .select(Columns.ALL)
                    .decodeList<AdoptionPetUI>()

                Log.d(TAG, "📦 Respuesta: ${response.size} mascotas totales")

                response.forEachIndexed { index, pet ->
                    Log.d(TAG, "📋 [$index] ${pet.name} - Adoptada: ${pet.is_adopted}")
                }

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

                Log.d(TAG, "🔢 Total: ${response.size}, Disponibles: ${pets.size}")

                withContext(Dispatchers.Main) {
                    allPets = pets
                    applyFilters()
                    hideLoading()
                    swipeRefresh.isRefreshing = false

                    if (allPets.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContent()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR AL CARGAR: ${e.message}", e)

                withContext(Dispatchers.Main) {
                    hideLoading()
                    swipeRefresh.isRefreshing = false
                    showError("Error: ${e.message}")
                }
            }
        }
    }

    private fun applyFilters() {
        Log.d(TAG, "🔧 Aplicando filtros...")

        filteredPets = allPets
            .filter { pet ->
                val matchesSearch = if (currentSearchQuery.isEmpty()) {
                    true
                } else {
                    pet.name.contains(currentSearchQuery, ignoreCase = true) ||
                            pet.breed.contains(currentSearchQuery, ignoreCase = true)
                }

                val matchesAge = when (currentAgeFilter) {
                    AgeFilter.ALL -> true
                    AgeFilter.PUPPY -> pet.age in 0..2
                    AgeFilter.ADULT -> pet.age in 3..7
                    AgeFilter.SENIOR -> pet.age >= 8
                }

                matchesSearch && matchesAge
            }

        Log.d(TAG, "📊 Filtrados: ${filteredPets.size} de ${allPets.size}")
        updateUI()
    }

    private fun updateUI() {
        val countText = when (filteredPets.size) {
            0 -> "No hay mascotas disponibles"
            1 -> "1 mascota disponible"
            else -> "${filteredPets.size} mascotas disponibles"
        }
        tvResultCount.text = countText

        adoptionAdapter.updatePets(filteredPets)

        if (filteredPets.isEmpty()) {
            showEmptyState()
        } else {
            showContent()
        }
    }

    private fun clearFilters() {
        searchBar.text?.clear()
        chipGroupAge.clearCheck()
        currentSearchQuery = ""
        currentAgeFilter = AgeFilter.ALL
        applyFilters()
        Toast.makeText(this, "Filtros limpiados", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerViewPets.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        tvResultCount.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showContent() {
        recyclerViewPets.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        tvResultCount.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        recyclerViewPets.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        tvResultCount.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        recyclerViewPets.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        tvResultCount.visibility = View.GONE
        errorText.text = message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}