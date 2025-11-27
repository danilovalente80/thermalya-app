// File: ui/screens/CalendarScreen.kt
package com.example.thermalya.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.data.models.User
import com.example.thermalya.ui.components.*
import com.example.thermalya.ui.viewmodels.CalendarUiState
import com.example.thermalya.ui.viewmodels.CalendarViewModel
import com.example.thermalya.ui.viewmodels.CalendarViewMode
import org.threeten.bp.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    currentDipendente: User,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendario Appuntamenti",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9B6BA8),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con filtri dipendenti
            CalendarHeader(
                viewMode = uiState.viewMode,
                selectedDate = uiState.selectedDate,
                selectedWeekStart = uiState.selectedWeekStart,
                dipendenti = uiState.dipendenti,
                selectedDipendentiIds = uiState.selectedDipendenti,
                onViewModeChange = { mode ->
                    viewModel.setViewMode(mode)
                },
                onPrevious = {
                    if (uiState.viewMode == CalendarViewMode.DAY_MULTI) {
                        viewModel.goToPreviousDay()
                    } else {
                        viewModel.goToPreviousWeek()
                    }
                },
                onNext = {
                    if (uiState.viewMode == CalendarViewMode.DAY_MULTI) {
                        viewModel.goToNextDay()
                    } else {
                        viewModel.goToNextWeek()
                    }
                },
                onToday = {
                    viewModel.goToToday()
                },
                onToggleDipendente = { dipendenteId ->
                    viewModel.toggleDipendente(dipendenteId)
                },
                onSelectAll = {
                    if (uiState.selectedDipendenti.size == uiState.dipendenti.size) {
                        viewModel.deselectAllDipendenti()
                    } else {
                        viewModel.selectAllDipendenti()
                    }
                }
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Contenuto calendario
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        // Loading
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF9B6BA8))
                        }
                    }

                    uiState.errorMessage != null -> {
                        // Errore
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Errore: ${uiState.errorMessage}",
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadAppuntamenti() }) {
                                    Text("Riprova")
                                }
                            }
                        }
                    }

                    uiState.selectedDipendenti.isEmpty() -> {
                        // Nessuna dipendente selezionata
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Seleziona almeno una dipendente",
                                color = Color.Gray
                            )
                        }
                    }

                    else -> {
                        // Mostra calendario
                        val selectedDipendentiList = uiState.dipendenti.filter {
                            uiState.selectedDipendenti.contains(it.id)
                        }

                        when (uiState.viewMode) {
                            CalendarViewMode.WEEK_SINGLE, CalendarViewMode.WEEK_MULTI -> {
                                WeekTimelineView(
                                    weekStart = uiState.selectedWeekStart,
                                    selectedDipendenti = selectedDipendentiList,
                                    appuntamenti = uiState.appuntamenti,
                                    onAppointmentClick = { appuntamento ->
                                        viewModel.selectAppointment(appuntamento)
                                    },
                                    onEmptySlotClick = { date, hour, dipendente ->
                                        // Apri dialog nuovo appuntamento
                                        viewModel.showNewAppointmentDialog()
                                        android.util.Log.d(
                                            "CalendarScreen",
                                            "Nuovo appuntamento: $date $hour per ${dipendente.nome}"
                                        )
                                    }
                                )
                            }

                            CalendarViewMode.DAY_MULTI -> {
                                DayTimelineView(
                                    date = uiState.selectedDate,
                                    selectedDipendenti = selectedDipendentiList,
                                    appuntamenti = uiState.appuntamenti,
                                    onAppointmentClick = { appuntamento ->
                                        viewModel.selectAppointment(appuntamento)
                                    },
                                    onEmptySlotClick = { date, hour, dipendente ->
                                        // TODO: Apri dialog nuovo appuntamento
                                        android.util.Log.d(
                                            "CalendarScreen",
                                            "Nuovo appuntamento: $date $hour per ${dipendente.nome}"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Dialog Dettaglio Appuntamento
            if (uiState.showDetailDialog && uiState.selectedAppointment != null) {
                val selectedDipendente = uiState.dipendenti.find {
                    it.id == uiState.selectedAppointment!!.dipendentaId
                } ?: currentDipendente

                AppointmentDetailDialog(
                    appuntamento = uiState.selectedAppointment!!,
                    dipendente = selectedDipendente,
                    cliente = uiState.selectedCliente,
                    trattamento = uiState.selectedTrattamento,
                    onDismiss = { viewModel.hideDetailDialog() },
                    onEdit = {
                        // TODO: Implementare modifica appuntamento
                        viewModel.hideDetailDialog()
                    },
                    onDelete = {
                        viewModel.deleteAppointment(uiState.selectedAppointment!!.id)
                    }
                )
            }
        }
    }
}