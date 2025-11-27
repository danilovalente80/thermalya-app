
// File: data/models/Cliente.kt
package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Cliente(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val cognome: String = "",
    val telefono: String = "",
    val email: String = "",
    val dataRegistrazione: Date = Date(),
    val ultimaVisita: Date? = null,
    val note: String = "",
    val attiva: Boolean = true
) {
    fun nomeCompleto() = "$nome $cognome"
}