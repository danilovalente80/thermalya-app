// File: ui/components/CalendarHeader.kt
package com.example.thermalya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.data.models.User
import com.example.thermalya.ui.viewmodels.CalendarViewMode
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle as ThreeTenTextStyle
import java.util.*

@Composable
fun CalendarHeader(
    viewMode: CalendarViewMode,
    selectedDate: LocalDate,
    selectedWeekStart: LocalDate,
    dipendenti: List<User>,
    selectedDipendentiIds: Set<String>,
    onViewModeChange: (CalendarViewMode) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onToggleDipendente: (String) -> Unit,
    onSelectAll: () -> Unit
) {
    val primaryColor = Color(0xFF9B6BA8)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Riga 1: Navigazione e data
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bottone Indietro
            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Precedente",
                    tint = primaryColor
                )
            }

            // Selector modalità + Data
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toggle modalità visualizzazione
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ViewModeButton(
                        text = "Settimana",
                        isSelected = viewMode != CalendarViewMode.DAY_MULTI,
                        onClick = {
                            if (selectedDipendentiIds.size <= 1) {
                                onViewModeChange(CalendarViewMode.WEEK_SINGLE)
                            } else {
                                onViewModeChange(CalendarViewMode.WEEK_MULTI)
                            }
                        }
                    )

                    ViewModeButton(
                        text = "Giorno",
                        isSelected = viewMode == CalendarViewMode.DAY_MULTI,
                        onClick = { onViewModeChange(CalendarViewMode.DAY_MULTI) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Data visualizzata
                Text(
                    text = when (viewMode) {
                        CalendarViewMode.WEEK_SINGLE, CalendarViewMode.WEEK_MULTI -> {
                            val endDate = selectedWeekStart.plusDays(6)
                            val monthName = when (selectedWeekStart.monthValue) {
                                1 -> "Gennaio"
                                2 -> "Febbraio"
                                3 -> "Marzo"
                                4 -> "Aprile"
                                5 -> "Maggio"
                                6 -> "Giugno"
                                7 -> "Luglio"
                                8 -> "Agosto"
                                9 -> "Settembre"
                                10 -> "Ottobre"
                                11 -> "Novembre"
                                12 -> "Dicembre"
                                else -> ""
                            }
                            "${selectedWeekStart.dayOfMonth} - ${endDate.dayOfMonth} $monthName ${selectedWeekStart.year}"
                        }
                        CalendarViewMode.DAY_MULTI -> {
                            selectedDate.format(
                                DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN)
                            )
                        }
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    textAlign = TextAlign.Center
                )
            }

            // Bottone Avanti
            IconButton(
                onClick = onNext,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Successivo",
                    tint = primaryColor
                )
            }

            // Bottone Oggi
            IconButton(
                onClick = onToday,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Oggi",
                    tint = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Riga 2: Toggle dipendenti
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dipendenti.forEach { dipendente ->
                DipendenteChip(
                    dipendente = dipendente,
                    isSelected = selectedDipendentiIds.contains(dipendente.id),
                    onClick = { onToggleDipendente(dipendente.id) }
                )
            }

            // Bottone "Tutte"
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectAll() }
                    .border(
                        width = 1.dp,
                        color = if (selectedDipendentiIds.size == dipendenti.size)
                            primaryColor else Color.LightGray,
                        shape = RoundedCornerShape(20.dp)
                    ),
                color = if (selectedDipendentiIds.size == dipendenti.size)
                    primaryColor.copy(alpha = 0.1f) else Color.Transparent
            ) {
                Text(
                    text = "Tutte",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedDipendentiIds.size == dipendenti.size)
                        primaryColor else Color.Gray
                )
            }
        }
    }
}

@Composable
fun ViewModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF9B6BA8)

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) primaryColor else Color.LightGray.copy(alpha = 0.3f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

@Composable
fun DipendenteChip(
    dipendente: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val chipColor = try {
        Color(android.graphics.Color.parseColor(dipendente.colore))
    } catch (e: Exception) {
        Color(0xFF9B6BA8)
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isSelected) chipColor else Color.LightGray,
                shape = RoundedCornerShape(20.dp)
            ),
        color = if (isSelected) chipColor.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f)
    ) {
        Text(
            text = dipendente.nome,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) chipColor else Color.Gray
        )
    }
}