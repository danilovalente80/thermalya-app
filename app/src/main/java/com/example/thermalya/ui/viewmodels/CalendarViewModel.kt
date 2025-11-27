// File: ui/viewmodels/CalendarViewModel.kt
package com.example.thermalya.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thermalya.data.models.Appuntamento
import com.example.thermalya.data.models.Cliente
import com.example.thermalya.data.models.Trattamento
import com.example.thermalya.data.models.User
import com.example.thermalya.data.repository.FirestoreAppuntamentoRepository
import com.example.thermalya.data.repository.FirestoreClienteRepository
import com.example.thermalya.data.repository.FirestoreTrattamentoRepository
import com.example.thermalya.data.repository.FirestoreUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

enum class CalendarViewMode {
    WEEK_SINGLE,
    WEEK_MULTI,
    DAY_MULTI
}

data class CalendarUiState(
    val viewMode: CalendarViewMode = CalendarViewMode.WEEK_SINGLE,
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedWeekStart: LocalDate = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong() - 1),
    val dipendenti: List<User> = emptyList(),
    val selectedDipendenti: Set<String> = emptySet(),
    val appuntamenti: List<Appuntamento> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNewAppointmentDialog: Boolean = false,
    val showDetailDialog: Boolean = false,
    val selectedAppointment: Appuntamento? = null,
    val selectedCliente: Cliente? = null,
    val selectedTrattamento: Trattamento? = null
)

class CalendarViewModel(context: Context) : ViewModel() {

    private val userRepository = FirestoreUserRepository()
    private val appuntamentoRepository = FirestoreAppuntamentoRepository()
    private val clienteRepository = FirestoreClienteRepository()
    private val trattamentoRepository = FirestoreTrattamentoRepository()

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        loadDipendenti()
    }

    private fun loadDipendenti() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = userRepository.getAllDipendenti()

            result.onSuccess { dipendenti ->
                val firstDipendenteId = dipendenti.firstOrNull()?.id
                _uiState.value = _uiState.value.copy(
                    dipendenti = dipendenti,
                    selectedDipendenti = if (firstDipendenteId != null) setOf(firstDipendenteId) else emptySet(),
                    isLoading = false
                )

                loadAppuntamenti()
            }.onFailure { exception ->
                android.util.Log.e("CalendarVM", "Errore caricamento dipendenti", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore caricamento dipendenti: ${exception.message}"
                )
            }
        }
    }

    fun loadAppuntamenti() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val state = _uiState.value
            val startDate = when (state.viewMode) {
                CalendarViewMode.WEEK_SINGLE, CalendarViewMode.WEEK_MULTI -> state.selectedWeekStart
                CalendarViewMode.DAY_MULTI -> state.selectedDate
            }

            val endDate = when (state.viewMode) {
                CalendarViewMode.WEEK_SINGLE, CalendarViewMode.WEEK_MULTI -> state.selectedWeekStart.plusDays(6)
                CalendarViewMode.DAY_MULTI -> state.selectedDate
            }

            val result = appuntamentoRepository.getAppuntamentiByDateRange(
                startDate = startDate,
                endDate = endDate,
                dipendenteIds = state.selectedDipendenti.toList()
            )

            result.onSuccess { appuntamenti ->
                android.util.Log.d("CalendarVM", "Appuntamenti caricati: ${appuntamenti.size}")
                _uiState.value = _uiState.value.copy(
                    appuntamenti = appuntamenti,
                    isLoading = false
                )
            }.onFailure { exception ->
                android.util.Log.e("CalendarVM", "Errore caricamento appuntamenti", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Errore: ${exception.message}"
                )
            }
        }
    }

    fun setViewMode(mode: CalendarViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
        loadAppuntamenti()
    }

    fun toggleDipendente(dipendenteId: String) {
        val currentSelected = _uiState.value.selectedDipendenti.toMutableSet()

        if (currentSelected.contains(dipendenteId)) {
            currentSelected.remove(dipendenteId)
        } else {
            currentSelected.add(dipendenteId)
        }

        _uiState.value = _uiState.value.copy(selectedDipendenti = currentSelected)
        loadAppuntamenti()
    }

    fun selectAllDipendenti() {
        val allIds = _uiState.value.dipendenti.map { it.id }.toSet()
        _uiState.value = _uiState.value.copy(selectedDipendenti = allIds)
        loadAppuntamenti()
    }

    fun deselectAllDipendenti() {
        _uiState.value = _uiState.value.copy(selectedDipendenti = emptySet())
        loadAppuntamenti()
    }

    fun goToPreviousWeek() {
        val newWeekStart = _uiState.value.selectedWeekStart.minusWeeks(1)
        _uiState.value = _uiState.value.copy(selectedWeekStart = newWeekStart)
        loadAppuntamenti()
    }

    fun goToNextWeek() {
        val newWeekStart = _uiState.value.selectedWeekStart.plusWeeks(1)
        _uiState.value = _uiState.value.copy(selectedWeekStart = newWeekStart)
        loadAppuntamenti()
    }

    fun goToToday() {
        val today = LocalDate.now()
        val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        _uiState.value = _uiState.value.copy(
            selectedDate = today,
            selectedWeekStart = weekStart
        )
        loadAppuntamenti()
    }

    fun goToPreviousDay() {
        val newDate = _uiState.value.selectedDate.minusDays(1)
        _uiState.value = _uiState.value.copy(selectedDate = newDate)
        loadAppuntamenti()
    }

    fun goToNextDay() {
        val newDate = _uiState.value.selectedDate.plusDays(1)
        _uiState.value = _uiState.value.copy(selectedDate = newDate)
        loadAppuntamenti()
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadAppuntamenti()
    }

    fun showNewAppointmentDialog() {
        _uiState.value = _uiState.value.copy(showNewAppointmentDialog = true)
    }

    fun hideNewAppointmentDialog() {
        _uiState.value = _uiState.value.copy(showNewAppointmentDialog = false)
    }

    fun selectAppointment(appointment: Appuntamento) {
        _uiState.value = _uiState.value.copy(
            selectedAppointment = appointment,
            showDetailDialog = true
        )

        viewModelScope.launch {
            val clienteResult = clienteRepository.getCliente(appointment.clienteId)
            clienteResult.onSuccess { cliente ->
                _uiState.value = _uiState.value.copy(selectedCliente = cliente)
            }

            val trattamentoResult = trattamentoRepository.getTrattamento(appointment.trattamentoId)
            trattamentoResult.onSuccess { trattamento ->
                _uiState.value = _uiState.value.copy(selectedTrattamento = trattamento)
            }
        }
    }

    fun hideDetailDialog() {
        _uiState.value = _uiState.value.copy(
            showDetailDialog = false,
            selectedAppointment = null,
            selectedCliente = null,
            selectedTrattamento = null
        )
    }

    fun deleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            val result = appuntamentoRepository.deleteAppuntamento(appointmentId)
            result.onSuccess {
                hideDetailDialog()
                loadAppuntamenti()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Errore eliminazione: ${exception.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}