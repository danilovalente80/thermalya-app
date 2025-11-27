// File: ui/components/AppointmentDetailDialog.kt
package com.example.thermalya.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.thermalya.data.models.Appuntamento
import com.example.thermalya.data.models.Cliente
import com.example.thermalya.data.models.Trattamento
import com.example.thermalya.data.models.User
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentDetailDialog(
    appuntamento: Appuntamento,
    dipendente: User,
    cliente: Cliente?,
    trattamento: Trattamento?,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dettaglio Appuntamento",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9B6BA8)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Chiudi",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Card Cliente
                InfoCard(
                    icon = Icons.Filled.Person,
                    title = "Cliente",
                    content = cliente?.nomeCompleto() ?: "Caricamento...",
                    subtitle = cliente?.telefono
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card Dipendente
                InfoCard(
                    icon = Icons.Filled.Person,
                    title = "Dipendente",
                    content = "${dipendente.nome} ${dipendente.cognome}",
                    color = parseColor(dipendente.colore)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card Data e Ora
                InfoCard(
                    icon = Icons.Filled.DateRange,
                    title = "Data e Ora",
                    content = dateFormat.format(appuntamento.data),
                    subtitle = "${appuntamento.oraInizio} - ${appuntamento.oraFine}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card Trattamento
                InfoCard(
                    icon = Icons.Filled.Build,
                    title = "Trattamento",
                    content = trattamento?.nome ?: "Caricamento...",
                    subtitle = trattamento?.let { "€${it.prezzo} - ${it.durataMinuti} min" }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card Prezzo
                InfoCard(
                    icon = Icons.Filled.AttachMoney,
                    title = "Prezzo",
                    content = "€ ${String.format("%.2f", appuntamento.prezzo)}",
                    color = Color(0xFF4CAF50)
                )

                // Note (se presenti)
                if (appuntamento.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Note",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = appuntamento.note,
                                fontSize = 14.sp,
                                color = Color(0xFF2C2C2C)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottoni Azioni
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bottone Elimina
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE57373)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE57373))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Elimina",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Elimina")
                    }

                    // Bottone Modifica
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9B6BA8)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Modifica",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Modifica")
                    }
                }
            }
        }
    }

    // Dialog Conferma Eliminazione
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Elimina Appuntamento",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Sei sicura di voler eliminare questo appuntamento?\n\n" +
                            "Cliente: ${cliente?.nomeCompleto() ?: "Sconosciuto"}\n" +
                            "Data: ${dateFormat.format(appuntamento.data)}\n" +
                            "Ora: ${appuntamento.oraInizio}\n\n" +
                            "Questa azione non può essere annullata."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373)
                    )
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annulla")
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String,
    subtitle: String? = null,
    color: Color = Color(0xFF9B6BA8)
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = content,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}