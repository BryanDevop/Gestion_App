package com.boxing.gestioncanina.ui.adoption

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxing.gestioncanina.data.models.AdoptionPetUI
import com.boxing.gestioncanina.data.repository.AdoptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdoptionPetsUiState(
    val pets: List<AdoptionPetUI> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdoptionPetsViewModel : ViewModel() {

    companion object {
        private const val TAG = "AdoptionPetsViewModel"
    }

    private val repository = AdoptionRepository()

    private val _uiState = MutableStateFlow(AdoptionPetsUiState())
    val uiState: StateFlow<AdoptionPetsUiState> = _uiState.asStateFlow()

    private var allPets: List<AdoptionPetUI> = emptyList()
    private var currentSearchQuery: String = ""
    private var currentAgeFilter: String? = null

    fun loadPets() {
        Log.d(TAG, "📡 Cargando mascotas en adopción...")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val pets = repository.getAvailablePets()
                allPets = pets

                Log.d(TAG, "✅ ${pets.size} mascotas cargadas")

                _uiState.value = _uiState.value.copy(
                    pets = pets,
                    isLoading = false,
                    error = null
                )

                applyFilters()

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cargar mascotas: ${e.message}", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar mascotas: ${e.message}"
                )
            }
        }
    }

    fun searchPets(query: String) {
        Log.d(TAG, "🔍 Buscando: '$query'")
        currentSearchQuery = query
        applyFilters()
    }

    fun filterByAge(ageFilter: String?) {
        Log.d(TAG, "🔢 Filtro de edad: $ageFilter")
        currentAgeFilter = ageFilter
        applyFilters()
    }

    fun clearFilters() {
        Log.d(TAG, "🧹 Limpiando filtros")
        currentSearchQuery = ""
        currentAgeFilter = null
        applyFilters()
    }

    private fun applyFilters() {
        var filteredPets = allPets

        // Filtrar por búsqueda
        if (currentSearchQuery.isNotEmpty()) {
            filteredPets = filteredPets.filter { pet ->
                pet.name.contains(currentSearchQuery, ignoreCase = true) ||
                        pet.breed.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // Filtrar por edad
        if (currentAgeFilter != null) {
            filteredPets = filteredPets.filter { pet ->
                when (currentAgeFilter) {
                    "puppy" -> pet.age <= 1
                    "adult" -> pet.age in 2..7
                    "senior" -> pet.age >= 8
                    else -> true
                }
            }
        }

        Log.d(TAG, "📊 Resultados filtrados: ${filteredPets.size} de ${allPets.size}")

        _uiState.value = _uiState.value.copy(pets = filteredPets)
    }
}