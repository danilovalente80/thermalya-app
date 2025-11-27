// File: ui/screens/HomeScreen.kt
package com.example.thermalya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.thermalya.data.models.User
import com.example.thermalya.data.repository.DatabaseInitializer
import java.util.Locale
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle as ThreeTenTextStyle

@Composable
fun HomeScreen(
    currentDipendente: User,
    onNavigateToClienti: () -> Unit,
    onNavigateToAppuntamenti: () -> Unit,
    onLogout: () -> Unit
) {
    val primaryColor = Color(0xFF9B6BA8)
    val primaryLight = Color(0xFFD4A5D9)
    val backgroundColor = Color.White
    val textColor = Color(0xFF2C2C2C)

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showInitButton by remember { mutableStateOf(true) }
    var initMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = primaryColor,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thermalya",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Benvenuta, ${currentDipendente.nome}",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = LocalDate.now().format(
                            org.threeten.bp.format.DateTimeFormatter.ofPattern(
                                "EEEE, d MMMM yyyy",
                                Locale.ITALIAN
                            )
                        ),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Contenuto scrollabile
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Bottone temporaneo per inizializzare DB
            if (showInitButton) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚙️ Inizializza Database",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Popola il database con dati di esempio (clienti, trattamenti, prodotti)",
                            fontSize = 13.sp,
                            color = Color(0xFFE65100).copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                DatabaseInitializer.initializeDatabase(context) { success, message ->
                                    initMessage = message
                                    if (success) showInitButton = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Inizializza Ora")
                        }
                        if (initMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = initMessage,
                                fontSize = 12.sp,
                                color = if (initMessage.contains("successo"))
                                    Color(0xFF4CAF50) else Color(0xFFE57373)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Cosa vuoi fare oggi?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Card Gestione Clienti
            MenuCard(
                title = "Gestione Clienti",
                description = "Visualizza, aggiungi o modifica le tue clienti",
                icon = Icons.Filled.Person,
                gradientColors = listOf(
                    Color(0xFF9B6BA8),
                    Color(0xFFB88BC4)
                ),
                onClick = onNavigateToClienti
            )

            // Card Gestione Appuntamenti
            MenuCard(
                title = "Gestione Appuntamenti",
                description = "Visualizza e gestisci il calendario appuntamenti",
                icon = Icons.Filled.DateRange,
                gradientColors = listOf(
                    Color(0xFFD4A5D9),
                    Color(0xFFE6B3E0)
                ),
                onClick = onNavigateToAppuntamenti
            )

            // Card Trattamenti (placeholder per futuro)
            MenuCard(
                title = "Trattamenti & Prodotti",
                description = "Gestisci trattamenti e prodotti disponibili",
                icon = Icons.Filled.ShoppingCart,
                gradientColors = listOf(
                    Color(0xFFE6B3D3),
                    Color(0xFFF0C9E0)
                ),
                onClick = { /* TODO: Implementare */ },
                enabled = false
            )

            // Card Report (placeholder per futuro)
            MenuCard(
                title = "Report & Statistiche",
                description = "Visualizza report e analisi delle attività",
                icon = Icons.Filled.Info,
                gradientColors = listOf(
                    Color(0xFFB0A8D0),
                    Color(0xFFC5BEE0)
                ),
                onClick = { /* TODO: Implementare */ },
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = if (enabled) 8.dp else 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (enabled) gradientColors else listOf(
                            Color.LightGray.copy(alpha = 0.5f),
                            Color.LightGray.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icona
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.3f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(36.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Testo
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )

                    if (!enabled) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prossimamente",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Freccia
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Vai",
                    tint = Color.White.copy(alpha = if (enabled) 0.8f else 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}