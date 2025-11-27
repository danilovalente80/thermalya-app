// File: ui/components/DayTimelineView.kt
package com.example.thermalya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.data.models.Appuntamento
import com.example.thermalya.data.models.User
import org.threeten.bp.LocalDate

@Composable
fun DayTimelineView(
    date: LocalDate,
    selectedDipendenti: List<User>,
    appuntamenti: List<Appuntamento>,
    onAppointmentClick: (Appuntamento) -> Unit,
    onEmptySlotClick: (LocalDate, String, User) -> Unit,
    modifier: Modifier = Modifier
) {
    val workingHours = (8..20).toList() // 08:00 - 20:00
    val hourHeight = 60.dp

    Row(modifier = modifier.fillMaxSize()) {
        // Colonna orari
        LazyColumn(
            modifier = Modifier.width(50.dp)
        ) {
            // Header vuoto
            item {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                )
            }

            // Ore
            items(workingHours.size) { index ->
                val hour = workingHours[index]
                Box(
                    modifier = Modifier
                        .height(hourHeight)
                        .fillMaxWidth()
                        .border(0.5.dp, Color.LightGray),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = String.format("%02d:00", hour),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Colonne dipendenti
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            selectedDipendenti.forEach { dipendente ->
                DipendenteColumn(
                    date = date,
                    dipendente = dipendente,
                    workingHours = workingHours,
                    hourHeight = hourHeight,
                    appuntamenti = appuntamenti.filter {
                        it.dipendentaId == dipendente.id
                    },
                    onAppointmentClick = onAppointmentClick,
                    onEmptySlotClick = onEmptySlotClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DipendenteColumn(
    date: LocalDate,
    dipendente: User,
    workingHours: List<Int>,
    hourHeight: Dp,
    appuntamenti: List<Appuntamento>,
    onAppointmentClick: (Appuntamento) -> Unit,
    onEmptySlotClick: (LocalDate, String, User) -> Unit,
    modifier: Modifier = Modifier
) {
    val dipendenteColor = parseColor(dipendente.colore)

    LazyColumn(modifier = modifier) {
        // Header con nome dipendente
        item {
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(dipendenteColor.copy(alpha = 0.2f))
                    .border(0.5.dp, Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dipendente.nome,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dipendenteColor
                )
            }
        }

        // Slot orari
        items(workingHours.size) { index ->
            val hour = workingHours[index]
            val hourString = String.format("%02d:00", hour)

            // Trova appuntamenti in questa fascia oraria
            val hourAppointments = appuntamenti.filter { app ->
                val appHour = app.oraInizio.split(":")[0].toIntOrNull() ?: 0
                appHour == hour
            }

            Box(
                modifier = Modifier
                    .height(hourHeight)
                    .fillMaxWidth()
                    .border(0.5.dp, Color.LightGray)
                    .clickable {
                        if (hourAppointments.isEmpty()) {
                            onEmptySlotClick(date, hourString, dipendente)
                        }
                    }
                    .background(Color.White)
            ) {
                // Mostra appuntamenti
                hourAppointments.forEach { appuntamento ->
                    DayAppointmentBlock(
                        appuntamento = appuntamento,
                        dipendente = dipendente,
                        onClick = { onAppointmentClick(appuntamento) }
                    )
                }
            }
        }
    }
}

@Composable
fun DayAppointmentBlock(
    appuntamento: Appuntamento,
    dipendente: User,
    onClick: () -> Unit
) {
    val blockColor = parseColor(dipendente.colore)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        color = blockColor.copy(alpha = 0.8f),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "${appuntamento.oraInizio} - ${appuntamento.oraFine}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            // TODO: Caricare nome cliente reale
            Text(
                text = "Cliente", // Sostituire con nome reale
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // TODO: Caricare trattamento reale
            Text(
                text = "Trattamento", // Sostituire con trattamento reale
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}