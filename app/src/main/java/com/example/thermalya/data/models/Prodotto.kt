package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId

data class Prodotto(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val categoria: String = "", // "smalti", "gel", "creme", etc
    val prezzo: Double = 0.0,
    val descrizione: String = "",
    val attivo: Boolean = true
)