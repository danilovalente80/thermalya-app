package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Transazione(
    @DocumentId
    val id: String = "",
    val clienteId: String = "",
    val data: Date = Date(),
    val tipo: String = "", // "appuntamento", "prodotto"
    val descrizione: String = "",
    val importo: Double = 0.0,
    val dipendentaId: String = "",
    val appuntamentoId: String? = null,
    val prodottoId: String? = null,
    val metodoPagamento: String = "", // "contanti", "carta", "online"
    val note: String = ""
)
