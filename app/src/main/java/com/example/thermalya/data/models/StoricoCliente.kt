package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ProdottoAcquistato(
    val data: Date = Date(),
    val prodottoId: String = "",
    val prodottoNome: String = "",
    val quantita: Int = 1,
    val prezzo: Double = 0.0
)

data class TrattamentoEffettuato(
    val data: Date = Date(),
    val trattamentoId: String = "",
    val trattamentoNome: String = "",
    val dipendentaId: String = "",
    val dipendentaNome: String = "",
    val costo: Double = 0.0,
    val note: String = ""
)

data class StoricoCliente(
    @DocumentId
    val clienteId: String = "",
    val prodottiAcquistati: List<ProdottoAcquistato> = emptyList(),
    val trattamentiEffettuati: List<TrattamentoEffettuato> = emptyList(),
    val noteGenerali: String = ""
)