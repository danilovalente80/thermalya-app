// File: ui/screens/ClientiScreen.kt
package com.example.thermalya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.data.models.Cliente
import com.example.thermalya.ui.viewmodels.ClientiViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientiScreen(
    viewModel: ClientiViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = Color(0xFF9B6BA8)
    val primaryLight = Color(0xFFD4A5D9)
    val backgroundColor = Color(0xFFF8F8F8)
    val textColor = Color(0xFF2C2C2C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestione Clienti",
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
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Aggiungi Cliente",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra di ricerca
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Loading
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            }
            // Error
            else if (uiState.errorMessage != null) {
                ErrorMessage(
                    message = uiState.errorMessage!!,
                    onDismiss = { viewModel.clearError() }
                )
            }
            // Lista vuota
            else if (uiState.clienti.isEmpty()) {
                EmptyState(
                    message = if (uiState.searchQuery.isEmpty())
                        "Nessuna cliente ancora.\nClicca + per aggiungerne una!"
                    else
                        "Nessun risultato per '${uiState.searchQuery}'"
                )
            }
            // Lista clienti
            else {
                ClientiList(
                    clienti = uiState.clienti,
                    onClienteClick = { cliente ->
                        viewModel.onClienteSelected(cliente)
                        viewModel.showEditDialog(cliente)
                    },
                    onDeleteClick = { cliente ->
                        viewModel.showDeleteDialog(cliente)
                    }
                )
            }
        }

        // Dialoghi
        if (uiState.showAddDialog) {
            AddClienteDialog(
                onDismiss = { viewModel.hideAddDialog() },
                onConfirm = { cliente ->
                    viewModel.addCliente(cliente)
                }
            )
        }

        if (uiState.showEditDialog && uiState.selectedCliente != null) {
            EditClienteDialog(
                cliente = uiState.selectedCliente!!,
                onDismiss = { viewModel.hideEditDialog() },
                onConfirm = { cliente ->
                    viewModel.updateCliente(cliente)
                }
            )
        }

        if (uiState.showDeleteDialog && uiState.selectedCliente != null) {
            DeleteConfirmDialog(
                clienteNome = uiState.selectedCliente!!.nomeCompleto(),
                onDismiss = { viewModel.hideDeleteDialog() },
                onConfirm = {
                    viewModel.deleteCliente(uiState.selectedCliente!!.id)
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Cerca per nome, cognome, telefono...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Cerca",
                tint = Color(0xFF9B6BA8)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Cancella",
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF9B6BA8),
            unfocusedBorderColor = Color(0xFFD4A5D9)
        )
    )
}

@Composable
fun ClientiList(
    clienti: List<Cliente>,
    onClienteClick: (Cliente) -> Unit,
    onDeleteClick: (Cliente) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(clienti, key = { it.id }) { cliente ->
            ClienteCard(
                cliente = cliente,
                onClick = { onClienteClick(cliente) },
                onDeleteClick = { onDeleteClick(cliente) }
            )
        }
    }
}

@Composable
fun ClienteCard(
    cliente: Cliente,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val primaryColor = Color(0xFF9B6BA8)
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar iniziali
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${cliente.nome.firstOrNull()?.uppercase() ?: ""}${cliente.cognome.firstOrNull()?.uppercase() ?: ""}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info cliente
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.nomeCompleto(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (cliente.telefono.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = cliente.telefono,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (cliente.ultimaVisita != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ultima visita: ${dateFormat.format(cliente.ultimaVisita!!)}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Bottone elimina
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Elimina",
                    tint = Color(0xFFE57373)
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFFEBEE)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color(0xFFC62828)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = Color(0xFFC62828),
                fontSize = 14.sp
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Chiudi",
                    tint = Color(0xFFC62828)
                )
            }
        }
    }
}