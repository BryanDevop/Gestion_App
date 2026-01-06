package com.boxing.gestioncanina.veterinaria.ui.mascota.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.databinding.FragmentMascotaListBinding
import com.boxing.gestioncanina.veterinaria.data.local.VeterinariaDatabase
import com.boxing.gestioncanina.veterinaria.data.model.Mascota
import com.boxing.gestioncanina.veterinaria.data.repository.VeterinariaRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment que muestra la lista de mascotas registradas.
 *
 * Implementa búsqueda, filtrado y navegación a otras pantallas.
 */
class MascotaListFragment : Fragment() {

    private var _binding: FragmentMascotaListBinding? = null
    private val binding get() = _binding!!

    // ViewModel con inyección manual de dependencias
    private val viewModel: MascotaListViewModel by viewModels {
        val database = VeterinariaDatabase.getDatabase(requireContext())
        val repository = VeterinariaRepository(
            database.mascotaDao(),
            database.consultaVeterinariaDao()
        )
        MascotaListViewModelFactory(repository)
    }

    private lateinit var mascotaAdapter: MascotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMascotaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupFilterChips()
        observeViewModel()
    }

    /**
     * Configura el toolbar con navegación.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Configura el RecyclerView con su adapter.
     */
    private fun setupRecyclerView() {
        mascotaAdapter = MascotaAdapter()

        // Configurar los callbacks
        mascotaAdapter.onItemClick = { mascota ->
            navigateToDetail(mascota)
        }

        mascotaAdapter.onEditClick = { mascota ->
            navigateToEdit(mascota)
        }

        mascotaAdapter.onDeleteClick = { mascota ->
            showDeleteConfirmation(mascota)
        }

        mascotaAdapter.onHistorialClick = { mascota ->
            navigateToHistorial(mascota)
        }

        binding.recyclerViewMascotas.apply {
            adapter = mascotaAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    /**
     * Configura el SearchView para búsqueda en tiempo real.
     */
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchMascotas(newText ?: "")
                return true
            }
        })
    }

    /**
     * Configura los chips de filtrado por especie.
     */
    private fun setupFilterChips() {
        binding.chipTodos.setOnClickListener {
            viewModel.filterByEspecie(null)
        }

        binding.chipPerros.setOnClickListener {
            viewModel.filterByEspecie("Perro")
        }

        binding.chipGatos.setOnClickListener {
            viewModel.filterByEspecie("Gato")
        }

        binding.chipAves.setOnClickListener {
            viewModel.filterByEspecie("Ave")
        }

        binding.chipOtros.setOnClickListener {
            viewModel.filterByEspecie("Otro")
        }
    }



    /**
     * Observa los cambios en el ViewModel.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar lista de mascotas
                launch {
                    viewModel.mascotas.collect { mascotas ->
                        mascotaAdapter.submitList(mascotas)
                    }
                }

                // Observar estado de la UI
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
            }
        }
    }

    /**
     * Maneja los diferentes estados de la UI.
     */
    private fun handleUiState(state: MascotaListViewModel.UiState) {
        when (state) {
            is MascotaListViewModel.UiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerViewMascotas.visibility = View.GONE
                binding.layoutEmpty.visibility = View.GONE
            }
            is MascotaListViewModel.UiState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewMascotas.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            }
            is MascotaListViewModel.UiState.Empty -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewMascotas.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            }
            is MascotaListViewModel.UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                showError(state.message)
            }
        }
    }

    /**
     * Navega a la pantalla de detalle.
     */
    private fun navigateToDetail(mascota: Mascota) {
        val action = MascotaListFragmentDirections
          //  .actionMascotaListFragmentToMascotaDetailFragment(mascota.id)
       // findNavController().navigate(action)
    }

    /**
     * Navega a la pantalla de edición.
     */
    private fun navigateToEdit(mascota: Mascota) {
        val action = MascotaListFragmentDirections
           // .actionMascotaListFragmentToMascotaEditFragment(mascota.id)
       // findNavController().navigate(action)
    }

    class MascotaListFragmentDirections {
        companion object

    }

    /**
     * Navega al historial médico.
     */
    private fun navigateToHistorial(mascota: Mascota) {
        val action = MascotaListFragmentDirections
             // .actionMascotaListFragmentToHistorialMedicoFragment(mascota.id)
       // findNavController().navigate(action)
    }

    /**
     * Muestra diálogo de confirmación de eliminación.
     */
    private fun showDeleteConfirmation(mascota: Mascota) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar mascota")
            .setMessage("¿Estás seguro de que deseas eliminar a ${mascota.nombre}? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteMascota(mascota)
                showMessage("${mascota.nombre} ha sido eliminada")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Muestra un mensaje con Snackbar.
     */
    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Muestra un error con Snackbar.
     */
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Reintentar") {
                // Recargar datos
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



/**
 * Factory para crear el ViewModel con dependencias.
 */
class MascotaListViewModelFactory(
    private val repository: VeterinariaRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MascotaListViewModel::class.java)) {
            return MascotaListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}