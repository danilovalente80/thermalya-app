// File: ui/viewmodels/ClientiViewModel.kt
package com.example.thermalya.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thermalya.data.models.Cliente
import com.example.thermalya.data.repository.FirestoreClienteRepository
import com.example.thermalya.data.repository.IClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class ClientiUiState(
    val clienti: List<Cliente> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCliente: Cliente? = null,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class ClientiViewModel(context: Context) : ViewModel() {

    private val clienteRepository: IClienteRepository = FirestoreClienteRepository()

    private val _uiState = MutableStateFlow(ClientiUiState())
    val uiState: StateFlow<ClientiUiState> = _uiState

    init {
        loadClienti()
    }

    fun loadClienti() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = clienteRepository.getAllClienti()

            result.onSuccess { clienti ->
                android.util.Log.d("ClientiVM", "Clienti caricate: ${clienti.size}")
                _uiState.value = _uiState.value.copy(
                    clienti = clienti,
                    isLoading = false
                )
            }.onFailure { exception ->
                android.util.Log.e("ClientiVM", "Errore caricamento clienti", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore: ${exception.message}"
                )
            }
        }
    }

    fun searchClienti(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isLoading = true,
                errorMessage = null
            )

            val result = clienteRepository.searchClienti(query)

            result.onSuccess { clienti ->
                android.util.Log.d("ClientiVM", "Ricerca '$query': ${clienti.size} risultati")
                _uiState.value = _uiState.value.copy(
                    clienti = clienti,
                    isLoading = false
                )
            }.onFailure { exception ->
                android.util.Log.e("ClientiVM", "Errore ricerca clienti", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore ricerca: ${exception.message}"
                )
            }
        }
    }

    fun addCliente(cliente: Cliente) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = clienteRepository.addCliente(cliente)

            result.onSuccess { clienteId ->
                android.util.Log.d("ClientiVM", "Cliente aggiunto: $clienteId")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showAddDialog = false
                )
                loadClienti() // Ricarica la lista
            }.onFailure { exception ->
                android.util.Log.e("ClientiVM", "Errore aggiunta cliente", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore aggiunta: ${exception.message}"
                )
            }
        }
    }

    fun updateCliente(cliente: Cliente) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = clienteRepository.updateCliente(cliente.id, cliente)

            result.onSuccess {
                android.util.Log.d("ClientiVM", "Cliente aggiornato: ${cliente.id}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showEditDialog = false,
                    selectedCliente = null
                )
                loadClienti() // Ricarica la lista
            }.onFailure { exception ->
                android.util.Log.e("ClientiVM", "Errore aggiornamento cliente", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore aggiornamento: ${exception.message}"
                )
            }
        }
    }

    fun deleteCliente(clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = clienteRepository.deleteCliente(clienteId)

            result.onSuccess {
                android.util.Log.d("ClientiVM", "Cliente eliminato: $clienteId")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showDeleteDialog = false,
                    selectedCliente = null
                )
                loadClienti() // Ricarica la lista
            }.onFailure { exception ->
                android.util.Log.e("ClientiVM", "Errore eliminazione cliente", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore eliminazione: ${exception.message}"
                )
            }
        }
    }

    // UI Actions
    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length >= 2 || query.isEmpty()) {
            searchClienti(query)
        }
    }

    fun onClienteSelected(cliente: Cliente) {
        _uiState.value = _uiState.value.copy(selectedCliente = cliente)
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun showEditDialog(cliente: Cliente) {
        _uiState.value = _uiState.value.copy(
            selectedCliente = cliente,
            showEditDialog = true
        )
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            selectedCliente = null
        )
    }

    fun showDeleteDialog(cliente: Cliente) {
        _uiState.value = _uiState.value.copy(
            selectedCliente = cliente,
            showDeleteDialog = true
        )
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            selectedCliente = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}