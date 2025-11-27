// File: ui/components/WeekTimelineView.kt
package com.example.thermalya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.data.models.Appuntamento
import com.example.thermalya.data.models.User
import org.threeten.bp.LocalDate
import java.util.*

@Composable
fun WeekTimelineView(
    weekStart: LocalDate,
    selectedDipendenti: List<User>,
    appuntamenti: List<Appuntamento>,
    onAppointmentClick: (Appuntamento) -> Unit,
    onEmptySlotClick: (LocalDate, String, User) -> Unit,
    modifier: Modifier = Modifier
) {
    val workingHours = (8..20).toList()
    val daysOfWeek = (0..5).map { weekStart.plusDays(it.toLong()) }
    val hourHeight = 60.dp

    Row(modifier = modifier.fillMaxSize()) {
        // Colonna orari
        LazyColumn(
            modifier = Modifier.width(50.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                )
            }

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

        // Griglia giorni
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (selectedDipendenti.size == 1) {
                daysOfWeek.forEach { date ->
                    DayColumn(
                        date = date,
                        dipendente = selectedDipendenti.first(),
                        workingHours = workingHours,
                        hourHeight = hourHeight,
                        appuntamenti = appuntamenti.filter {
                            isSameDay(it.data, date) &&
                                    it.dipendentaId == selectedDipendenti.first().id
                        },
                        onAppointmentClick = onAppointmentClick,
                        onEmptySlotClick = onEmptySlotClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                daysOfWeek.forEach { date ->
                    Column(modifier = Modifier.weight(1f)) {
                        DayHeader(date = date)

                        Row {
                            selectedDipendenti.forEach { dipendente ->
                                DayColumn(
                                    date = date,
                                    dipendente = dipendente,
                                    workingHours = workingHours,
                                    hourHeight = hourHeight,
                                    appuntamenti = appuntamenti.filter {
                                        isSameDay(it.data, date) &&
                                                it.dipendentaId == dipendente.id
                                    },
                                    onAppointmentClick = onAppointmentClick,
                                    onEmptySlotClick = onEmptySlotClick,
                                    modifier = Modifier.weight(1f),
                                    showDipendenteInitial = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayHeader(date: LocalDate) {
    val dayOfWeekNames = listOf("", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
    val dayName = dayOfWeekNames[date.dayOfWeek.value]
    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .background(if (isToday) Color(0xFFD4A5D9) else Color(0xFFF5F5F5))
            .border(0.5.dp, Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isToday) Color.White else Color(0xFF2C2C2C)
            )
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isToday) Color.White else Color(0xFF9B6BA8)
            )
        }
    }
}

@Composable
fun DayColumn(
    date: LocalDate,
    dipendente: User,
    workingHours: List<Int>,
    hourHeight: Dp,
    appuntamenti: List<Appuntamento>,
    onAppointmentClick: (Appuntamento) -> Unit,
    onEmptySlotClick: (LocalDate, String, User) -> Unit,
    modifier: Modifier = Modifier,
    showDipendenteInitial: Boolean = false
) {
    LazyColumn(modifier = modifier) {
        if (showDipendenteInitial) {
            item {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(parseColor(dipendente.colore).copy(alpha = 0.2f))
                        .border(0.5.dp, Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dipendente.nome.first().toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = parseColor(dipendente.colore)
                    )
                }
            }
        } else {
            item {
                DayHeader(date = date)
            }
        }

        items(workingHours.size) { index ->
            val hour = workingHours[index]
            val hourString = String.format("%02d:00", hour)

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
                hourAppointments.forEach { appuntamento ->
                    AppointmentBlock(
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
fun AppointmentBlock(
    appuntamento: Appuntamento,
    dipendente: User,
    onClick: () -> Unit
) {
    val blockColor = parseColor(dipendente.colore)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        color = blockColor.copy(alpha = 0.8f),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = "${appuntamento.oraInizio}-${appuntamento.oraFine}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Cliente",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun isSameDay(date: Date, localDate: LocalDate): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.YEAR) == localDate.year &&
            calendar.get(Calendar.MONTH) + 1 == localDate.monthValue &&
            calendar.get(Calendar.DAY_OF_MONTH) == localDate.dayOfMonth
}

fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color(0xFF9B6BA8)
    }
}