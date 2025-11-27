// File: ui/screens/LoginScreen.kt
package com.example.thermalya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermalya.R
import com.example.thermalya.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: (com.example.thermalya.data.models.User) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember {
        LoginViewModel(context)
    }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showError by remember { mutableStateOf(true) }
    var expandedDropdown by remember { mutableStateOf(false) }

    // Colori Thermalya
    val primaryColor = Color(0xFF9B6BA8)
    val primaryLight = Color(0xFFD4A5D9)
    val backgroundColor = Color.White
    val textColor = Color(0xFF2C2C2C)

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            uiState.selectedDipendente?.let { onLoginSuccess(it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        primaryLight.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_thermalya),
                contentDescription = "Logo Thermalya",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Titolo
            /*
            Text(
                text = "Thermalya",
                fontSize = 32.sp,
                color = primaryColor,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            */

            Text(
                text = "Centro Estetico",
                fontSize = 16.sp,
                color = textColor.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Dipendenti
            if (uiState.isLoadingDipendenti) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = primaryColor,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Caricamento dipendenti...",
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.6f)
                )
            } else {
                // Dropdown Dipendenti
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = primaryLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { expandedDropdown = !expandedDropdown },
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.selectedDipendente?.nome ?: "Seleziona dipendente",
                                fontSize = 14.sp,
                                color = if (uiState.selectedDipendente == null)
                                    textColor.copy(alpha = 0.5f) else textColor,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Dropdown Menu
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        uiState.dipendenti.forEach { dipendente ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${dipendente.nome} ${dipendente.cognome}",
                                        fontSize = 14.sp,
                                        color = textColor
                                    )
                                },
                                onClick = {
                                    viewModel.onDipendenteSelected(dipendente)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Password",
                            tint = primaryColor
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = primaryLight,
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    ),
                    singleLine = true
                )

                // Error Message
                if (uiState.errorMessage != null && showError) {
                    LaunchedEffect(uiState.errorMessage) {
                        showError = true
                        delay(4000)
                        showError = false
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = uiState.errorMessage.orEmpty(),
                                color = Color(0xFFC62828),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Login Button
                Button(
                    onClick = { viewModel.login() },
                    enabled = !uiState.isLoading && uiState.selectedDipendente != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        disabledContainerColor = primaryColor.copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Accedi",
                            color = Color.White,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Info text
                Text(
                    text = "Usa le tue credenziali Thermalya per accedere",
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun Image(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = Color.Unspecified
    )
}