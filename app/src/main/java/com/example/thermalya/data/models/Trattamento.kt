package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId

data class Trattamento(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val categoria: String = "", // "semipermanente", "massaggio", "pulizia", etc
    val durataMinuti: Int = 30,
    val prezzo: Double = 0.0,
    val descrizione: String = "",
    val attivo: Boolean = true
)