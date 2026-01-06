package com.boxing.gestioncanina.veterinaria.ui.mascota.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxing.gestioncanina.veterinaria.data.model.Mascota
import com.boxing.gestioncanina.veterinaria.data.repository.VeterinariaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para la lista de mascotas.
 *
 * Maneja el estado de la UI y las operaciones de datos de forma reactiva.
 * Utiliza StateFlow para mantener el estado de la UI y emitir actualizaciones.
 */
class MascotaListViewModel(
    private val repository: VeterinariaRepository
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Lista de mascotas actual
    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    // Filtro de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtro de especie
    private val _especieFiltro = MutableStateFlow<String?>(null)
    val especieFiltro: StateFlow<String?> = _especieFiltro.asStateFlow()

    init {
        loadMascotas()
    }

    /**
     * Carga las mascotas desde el repositorio.
     * Aplica filtros de búsqueda y especie si están activos.
     */
    private fun loadMascotas() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // Combina los filtros con los datos
            combine(
                repository.getAllMascotas(),
                _searchQuery,
                _especieFiltro
            ) { mascotas, query, especie ->
                var filtered = mascotas

                // Aplicar filtro de especie
                if (especie != null) {
                    filtered = filtered.filter { it.especie.equals(especie, ignoreCase = true) }
                }

                // Aplicar filtro de búsqueda
                if (query.isNotBlank()) {
                    filtered = filtered.filter {
                        it.nombre.contains(query, ignoreCase = true) ||
                                it.raza.contains(query, ignoreCase = true) ||
                                it.nombreDueno.contains(query, ignoreCase = true)
                    }
                }

                filtered
            }.catch { exception ->
                _uiState.value = UiState.Error(exception.message ?: "Error desconocido")
            }.collect { filteredMascotas ->
                _mascotas.value = filteredMascotas
                _uiState.value = if (filteredMascotas.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success
                }
            }
        }
    }

    /**
     * Actualiza el query de búsqueda.
     */
    fun searchMascotas(query: String) {
        _searchQuery.value = query
    }

    /**
     * Filtra por especie. Null para mostrar todas.
     */
    fun filterByEspecie(especie: String?) {
        _especieFiltro.value = especie
    }

    /**
     * Elimina una mascota.
     */
    fun deleteMascota(mascota: Mascota) {
        viewModelScope.launch {
            try {
                repository.deleteMascota(mascota)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al eliminar: ${e.message}")
            }
        }
    }

    /**
     * Desactiva una mascota (soft delete).
     */
    fun deactivateMascota(mascotaId: Long) {
        viewModelScope.launch {
            try {
                repository.deactivateMascota(mascotaId)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al desactivar: ${e.message}")
            }
        }
    }

    /**
     * Estados posibles de la UI.
     */
    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }
}