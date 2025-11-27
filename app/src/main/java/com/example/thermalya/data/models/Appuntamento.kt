package com.example.thermalya.data.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Appuntamento(
    @DocumentId
    val id: String = "",
    val clienteId: String = "",
    val dipendentaId: String = "",
    val data: Date = Date(),
    val oraInizio: String = "", // "10:30"
    val oraFine: String = "", // "11:30"
    val trattamentoId: String = "",
    val note: String = "",
    val stato: String = "confermato", // "confermato", "cancellato", "completato"
    val googleCalendarEventId: String = "",
    val promemoriaInviato: Boolean = false,
    val prezzo: Double = 0.0
)