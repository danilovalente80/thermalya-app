// File: MainActivity.kt
package com.example.thermalya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thermalya.ui.screens.CalendarScreen
import com.example.thermalya.ui.screens.ClientiScreen
import com.example.thermalya.ui.screens.HomeScreen
import com.example.thermalya.ui.screens.LoginScreen
import com.example.thermalya.ui.theme.ThermalyaTheme
import com.example.thermalya.ui.viewmodels.CalendarViewModel
import com.example.thermalya.ui.viewmodels.ClientiViewModel

// Enum per gestire le diverse schermate
enum class Screen {
    LOGIN,
    HOME,
    CLIENTI,
    APPUNTAMENTI
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.jakewharton.threetenabp.AndroidThreeTen.init(this)

        setContent {
            ThermalyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by rememberSaveable { mutableStateOf(Screen.LOGIN) }
                    var currentDipendenteId by rememberSaveable { mutableStateOf<String?>(null) }
                    var currentDipendente by remember { mutableStateOf<com.example.thermalya.data.models.User?>(null) }

                    when (currentScreen) {
                        Screen.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = { dipendente ->
                                    currentDipendente = dipendente
                                    currentDipendenteId = dipendente.id
                                    currentScreen = Screen.HOME
                                }
                            )
                        }

                        Screen.HOME -> {
                            currentDipendente?.let { dipendente ->
                                HomeScreen(
                                    currentDipendente = dipendente,
                                    onNavigateToClienti = {
                                        currentScreen = Screen.CLIENTI
                                    },
                                    onNavigateToAppuntamenti = {
                                        currentScreen = Screen.APPUNTAMENTI
                                    },
                                    onLogout = {
                                        currentScreen = Screen.LOGIN
                                        currentDipendente = null
                                        currentDipendenteId = null
                                    }
                                )
                            }
                        }

                        Screen.CLIENTI -> {
                            currentDipendente?.let {
                                val clientiViewModel = remember { ClientiViewModel(this@MainActivity) }
                                ClientiScreen(
                                    viewModel = clientiViewModel,
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }
                        }

                        Screen.APPUNTAMENTI -> {
                            currentDipendente?.let { dipendente ->
                                val calendarViewModel = remember { CalendarViewModel(this@MainActivity) }
                                CalendarScreen(
                                    viewModel = calendarViewModel,
                                    currentDipendente = dipendente,
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Schermata placeholder temporanea
@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack) {
            Text("Torna alla Home")
        }
    }
}

@Composable
fun Text(text: String) {
    androidx.compose.material3.Text(text = text)
}