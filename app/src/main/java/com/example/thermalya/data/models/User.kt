package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val nome: String = "",
    val cognome: String = "",
    val ruolo: String = "", // "admin", "dipendente", "receptionist"
    val colore: String = "", // colore per identificare la dipendente
    val attiva: Boolean = true,
    val dataCreazione: Date = Date()
)