// File: ui/viewmodels/LoginViewModel.kt
package com.example.thermalya.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thermalya.data.models.User
import com.example.thermalya.data.repository.FirestoreUserRepository
import com.example.thermalya.data.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class LoginUiState(
    val dipendenti: List<User> = emptyList(),
    val selectedDipendente: User? = null,
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val isLoadingDipendenti: Boolean = true
)

class LoginViewModel(context: Context) : ViewModel() {

    // Usa direttamente Firestore (no Realm, no Factory)
    private val userRepository: IUserRepository = FirestoreUserRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        loadDipendenti()
    }

    private fun loadDipendenti() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDipendenti = true)

            val result = userRepository.getAllDipendenti()

            result.onSuccess { dipendenti ->
                android.util.Log.d("LoginVM", "Dipendenti caricate: ${dipendenti.size}")

                _uiState.value = _uiState.value.copy(
                    dipendenti = dipendenti,
                    isLoadingDipendenti = false,
                    selectedDipendente = if (dipendenti.isNotEmpty()) dipendenti[0] else null,
                    errorMessage = if (dipendenti.isEmpty()) "Nessuna dipendente trovata nel database" else null
                )
            }.onFailure { exception ->
                android.util.Log.e("LoginVM", "Errore caricamento dipendenti: ${exception.message}", exception)
                _uiState.value = _uiState.value.copy(
                    isLoadingDipendenti = false,
                    errorMessage = "Errore di connessione: ${exception.message}"
                )
            }
        }
    }

    fun onDipendenteSelected(dipendente: User) {
        _uiState.value = _uiState.value.copy(
            selectedDipendente = dipendente,
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login() {
        if (validateInputs()) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val selectedDipendente = _uiState.value.selectedDipendente ?: return@launch

                // Login semplificato per sviluppo
                // TODO: In produzione, integrare con Firebase Auth per verificare la password
                val result = try {
                    if (selectedDipendente.email.isNotEmpty()) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception("Email non valida"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Errore di login"
                    )
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val selectedDipendente = _uiState.value.selectedDipendente
        val password = _uiState.value.password

        return when {
            selectedDipendente == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Seleziona una dipendente")
                false
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Inserisci la password")
                false
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password troppo corta (min 6 caratteri)")
                false
            }
            else -> true
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Non serve più onCleared() perché non usiamo Realm
}