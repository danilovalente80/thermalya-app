// File: ui/screens/ClienteDialogs.kt
package com.example.thermalya.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.thermalya.data.models.Cliente
import java.util.Date

@Composable
fun AddClienteDialog(
    onDismiss: () -> Unit,
    onConfirm: (Cliente) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var nomeError by remember { mutableStateOf(false) }
    var cognomeError by remember { mutableStateOf(false) }

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
                // Titolo
                Text(
                    text = "Nuova Cliente",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9B6BA8)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Campo Nome
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        nomeError = false
                    },
                    label = { Text("Nome *") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null)
                    },
                    isError = nomeError,
                    supportingText = {
                        if (nomeError) Text("Campo obbligatorio")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Cognome
                OutlinedTextField(
                    value = cognome,
                    onValueChange = {
                        cognome = it
                        cognomeError = false
                    },
                    label = { Text("Cognome *") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null)
                    },
                    isError = cognomeError,
                    supportingText = {
                        if (cognomeError) Text("Campo obbligatorio")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Telefono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Telefono") },
                    leadingIcon = {
                        Icon(Icons.Filled.Phone, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    leadingIcon = {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bottoni
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF9B6BA8)
                        )
                    ) {
                        Text("Annulla")
                    }

                    Button(
                        onClick = {
                            // Validazione
                            if (nome.isBlank()) {
                                nomeError = true
                                return@Button
                            }
                            if (cognome.isBlank()) {
                                cognomeError = true
                                return@Button
                            }

                            // Crea cliente
                            val nuovaCliente = Cliente(
                                id = "", // Firestore genererà l'ID
                                nome = nome.trim(),
                                cognome = cognome.trim(),
                                telefono = telefono.trim(),
                                email = email.trim(),
                                note = note.trim(),
                                dataRegistrazione = Date(),
                                ultimaVisita = null,
                                attiva = true
                            )

                            onConfirm(nuovaCliente)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9B6BA8)
                        )
                    ) {
                        Text("Salva")
                    }
                }
            }
        }
    }
}

@Composable
fun EditClienteDialog(
    cliente: Cliente,
    onDismiss: () -> Unit,
    onConfirm: (Cliente) -> Unit
) {
    var nome by remember { mutableStateOf(cliente.nome) }
    var cognome by remember { mutableStateOf(cliente.cognome) }
    var telefono by remember { mutableStateOf(cliente.telefono) }
    var email by remember { mutableStateOf(cliente.email) }
    var note by remember { mutableStateOf(cliente.note) }

    var nomeError by remember { mutableStateOf(false) }
    var cognomeError by remember { mutableStateOf(false) }

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
                // Titolo
                Text(
                    text = "Modifica Cliente",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9B6BA8)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Campo Nome
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        nomeError = false
                    },
                    label = { Text("Nome *") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null)
                    },
                    isError = nomeError,
                    supportingText = {
                        if (nomeError) Text("Campo obbligatorio")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Cognome
                OutlinedTextField(
                    value = cognome,
                    onValueChange = {
                        cognome = it
                        cognomeError = false
                    },
                    label = { Text("Cognome *") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null)
                    },
                    isError = cognomeError,
                    supportingText = {
                        if (cognomeError) Text("Campo obbligatorio")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Telefono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Telefono") },
                    leadingIcon = {
                        Icon(Icons.Filled.Phone, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    leadingIcon = {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9B6BA8),
                        focusedLabelColor = Color(0xFF9B6BA8)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bottoni
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF9B6BA8)
                        )
                    ) {
                        Text("Annulla")
                    }

                    Button(
                        onClick = {
                            // Validazione
                            if (nome.isBlank()) {
                                nomeError = true
                                return@Button
                            }
                            if (cognome.isBlank()) {
                                cognomeError = true
                                return@Button
                            }

                            // Aggiorna cliente
                            val clienteAggiornata = cliente.copy(
                                nome = nome.trim(),
                                cognome = cognome.trim(),
                                telefono = telefono.trim(),
                                email = email.trim(),
                                note = note.trim()
                            )

                            onConfirm(clienteAggiornata)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9B6BA8)
                        )
                    ) {
                        Text("Salva")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    clienteNome: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                text = "Elimina Cliente",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Sei sicura di voler eliminare $clienteNome?\n\nQuesta azione non può essere annullata.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Text("Elimina")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Annulla")
            }
        },
        containerColor = Color.White
    )
}
