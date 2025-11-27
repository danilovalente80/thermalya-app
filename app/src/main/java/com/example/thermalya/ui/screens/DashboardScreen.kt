// File: ui/screens/DashboardScreen.kt
package com.example.thermalya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.data.models.User
import java.util.Locale

import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle

@Composable
fun DashboardScreen(
    currentDipendente: User,
    onLogout: () -> Unit,
    onAddAppointment: () -> Unit
) {
    val primaryColor = Color(0xFF9B6BA8)
    val primaryLight = Color(0xFFD4A5D9)
    val backgroundColor = Color.White
    val textColor = Color(0xFF2C2C2C)

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
    ) {
        // Header con info dipendente
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor),
            color = primaryColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bottone Indietro
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Calendario ${currentDipendente.nome}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = LocalDate.now().format(
                            org.threeten.bp.format.DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ITALIAN)
                        ),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Spacer per bilanciare il layout
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Calendario
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Header mese
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { currentMonth = currentMonth.minusMonths(1) },
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
                ) {
                    Text("◀", color = textColor)
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ITALIAN)} ${currentMonth.year}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = { currentMonth = currentMonth.plusMonths(1) },
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
                ) {
                    Text("▶", color = textColor)
                }
            }

            // Griglia calendario
            val firstDay = currentMonth.atDay(1)
            val lastDay = currentMonth.atEndOfMonth()
            val daysInMonth = lastDay.dayOfMonth
            val startingDayOfWeek = firstDay.dayOfWeek.value % 7 // 0 = Sunday

            val weekDays = listOf("Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab")

            // Header giorni della settimana
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.6f)
                    )
                }
            }

            // Giorni del mese
            val totalCells = (startingDayOfWeek + daysInMonth + 6) / 7 * 7
            val days = mutableListOf<LocalDate?>()

            repeat(startingDayOfWeek) { days.add(null) }
            repeat(daysInMonth) { days.add(firstDay.plusDays(it.toLong())) }
            repeat(totalCells - days.size) { days.add(null) }

            for (week in 0 until totalCells / 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayIndex in 0 until 7) {
                        val day = days[week * 7 + dayIndex]
                        val isSelected = day == selectedDate
                        val isToday = day == LocalDate.now()
                        val isCurrentMonth = day?.yearMonth == currentMonth

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .then(
                                    if (day != null)
                                        Modifier.background(
                                            color = when {
                                                isSelected -> primaryColor
                                                isToday -> primaryLight
                                                else -> if (isCurrentMonth) Color.White else Color.LightGray
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    else
                                        Modifier
                                ),
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isSelected -> primaryColor
                                isToday -> primaryLight
                                else -> if (isCurrentMonth) backgroundColor else Color.LightGray
                            }
                        ) {
                            if (day != null) {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(Alignment.Center),
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) Color.White else textColor,
                                    fontSize = 12.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Data selezionata e appuntamenti
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Appuntamenti del ${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.ITALIAN)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Placeholder appuntamenti (per ora vuoto)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = primaryLight.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nessun appuntamento",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottone aggiungi appuntamento
        Button(
            onClick = onAddAppointment,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Aggiungi",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aggiungi Appuntamento", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Extension per LocalDate
val org.threeten.bp.LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)