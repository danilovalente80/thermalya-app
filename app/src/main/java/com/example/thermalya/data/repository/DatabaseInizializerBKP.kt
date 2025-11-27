// File: data/repository/DatabaseInitializer.kt
package com.example.thermalya.data.repository

import android.content.Context
import com.example.thermalya.data.models.Appuntamento
import com.example.thermalya.data.models.Cliente
import com.example.thermalya.data.models.Prodotto
import com.example.thermalya.data.models.Trattamento
import com.example.thermalya.data.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Classe helper per inizializzare il database Firestore con dati di esempio
 * DA USARE SOLO UNA VOLTA PER POPOLARE IL DB
 */
object DatabaseInitializerBKP {

    fun initializeDatabase(context: Context, onComplete: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                android.util.Log.d("DBInit", "Inizio inizializzazione database...")

                // Repository
                val userRepo = FirestoreUserRepository()
                val clienteRepo = FirestoreClienteRepository()
                val trattamentoRepo = FirestoreTrattamentoRepository()
                val prodottoRepo = FirestoreProdottoRepository()
                val appuntamentoRepo = FirestoreAppuntamentoRepository()

                var successCount = 0
                var errorCount = 0

                // 1. DIPENDENTI (se non esistono già)
                val dipendentiResult = userRepo.getAllDipendenti()
                val dipendentiList = if (dipendentiResult.getOrNull()?.isEmpty() == true) {
                    android.util.Log.d("DBInit", "Creazione dipendenti...")
                    val dipList = createDipendenti()
                    dipList.forEach { dipendente ->
                        val result = userRepo.addUser(dipendente)
                        if (result.isSuccess) successCount++ else errorCount++
                    }
                    android.util.Log.d("DBInit", "Dipendenti create: ${dipList.size}")
                    dipList
                } else {
                    dipendentiResult.getOrNull() ?: emptyList()
                }

                // 2. CLIENTI
                android.util.Log.d("DBInit", "Creazione clienti...")
                val clienti = createClienti()
                val clientiList = mutableListOf<Cliente>()
                clienti.forEach { cliente ->
                    val result = clienteRepo.addCliente(cliente)
                    result.onSuccess { clienteId ->
                        clientiList.add(cliente.copy(id = clienteId))
                        successCount++
                    }
                    result.onFailure { errorCount++ }
                }
                android.util.Log.d("DBInit", "Clienti create: ${clientiList.size}")

                // 3. TRATTAMENTI
                android.util.Log.d("DBInit", "Creazione trattamenti...")
                val trattamenti = createTrattamenti()
                trattamenti.forEach { trattamento ->
                    val result = trattamentoRepo.addTrattamento(trattamento)
                    if (result.isSuccess) successCount++ else errorCount++
                }
                android.util.Log.d("DBInit", "Trattamenti creati: ${trattamenti.size}")

                // 4. PRODOTTI
                android.util.Log.d("DBInit", "Creazione prodotti...")
                val prodotti = createProdotti()
                prodotti.forEach { prodotto ->
                    val result = prodottoRepo.addProdotto(prodotto)
                    if (result.isSuccess) successCount++ else errorCount++
                }
                android.util.Log.d("DBInit", "Prodotti creati: ${prodotti.size}")

                // 5. APPUNTAMENTI DI TEST
                android.util.Log.d("DBInit", "Creazione appuntamenti di test...")
                val appuntamenti = createAppuntamenti(dipendentiList, clientiList)
                appuntamenti.forEach { appuntamento ->
                    val result = appuntamentoRepo.addAppuntamento(appuntamento)
                    if (result.isSuccess) successCount++ else errorCount++
                }
                android.util.Log.d("DBInit", "Appuntamenti creati: ${appuntamenti.size}")

                android.util.Log.d("DBInit", "Inizializzazione completata! Success: $successCount, Errors: $errorCount")
                onComplete(true, "Database inizializzato con successo!\n$successCount elementi creati")

            } catch (e: Exception) {
                android.util.Log.e("DBInit", "Errore inizializzazione database", e)
                onComplete(false, "Errore: ${e.message}")
            }
        }
    }

    private fun createDipendenti(): List<User> {
        return listOf(
            User(
                id = "",
                email = "valentina@thermalya.it",
                nome = "Valentina",
                cognome = "Rossi",
                ruolo = "dipendente",
                colore = "#FF69B4",
                attiva = true,
                dataCreazione = Date()
            ),
            User(
                id = "",
                email = "sara@thermalya.it",
                nome = "Sara",
                cognome = "Bianchi",
                ruolo = "dipendente",
                colore = "#00CED1",
                attiva = true,
                dataCreazione = Date()
            ),
            User(
                id = "",
                email = "silvia@thermalya.it",
                nome = "Silvia",
                cognome = "Verdi",
                ruolo = "dipendente",
                colore = "#FFD700",
                attiva = true,
                dataCreazione = Date()
            ),
            User(
                id = "",
                email = "admin@thermalya.it",
                nome = "Admin",
                cognome = "Thermalya",
                ruolo = "admin",
                colore = "#9B6BA8",
                attiva = true,
                dataCreazione = Date()
            )
        )
    }

    private fun createClienti(): List<Cliente> {
        return listOf(
            Cliente(
                id = "",
                nome = "Maria",
                cognome = "Rossi",
                telefono = "3331234567",
                email = "maria.rossi@email.it",
                dataRegistrazione = Date(),
                ultimaVisita = Date(),
                note = "Cliente abituale, preferisce appuntamenti al mattino",
                attiva = true
            ),
            Cliente(
                id = "",
                nome = "Giulia",
                cognome = "Bianchi",
                telefono = "3339876543",
                email = "giulia.bianchi@email.it",
                dataRegistrazione = Date(),
                ultimaVisita = null,
                note = "",
                attiva = true
            ),
            Cliente(
                id = "",
                nome = "Anna",
                cognome = "Verdi",
                telefono = "3345678901",
                email = "anna.verdi@email.it",
                dataRegistrazione = Date(),
                ultimaVisita = Date(),
                note = "Allergica a determinati prodotti",
                attiva = true
            ),
            Cliente(
                id = "",
                nome = "Laura",
                cognome = "Neri",
                telefono = "3351234789",
                email = "laura.neri@email.it",
                dataRegistrazione = Date(),
                ultimaVisita = null,
                note = "",
                attiva = true
            ),
            Cliente(
                id = "",
                nome = "Sofia",
                cognome = "Ferrari",
                telefono = "3367890123",
                email = "sofia.ferrari@email.it",
                dataRegistrazione = Date(),
                ultimaVisita = Date(),
                note = "VIP - sconti 10%",
                attiva = true
            )
        )
    }

    private fun createTrattamenti(): List<Trattamento> {
        return listOf(
            // Unghie
            Trattamento(
                id = "",
                nome = "Semipermanente Mani",
                categoria = "unghie",
                durataMinuti = 60,
                prezzo = 25.0,
                descrizione = "Applicazione smalto semipermanente con manicure base",
                attivo = true
            ),
            Trattamento(
                id = "",
                nome = "Semipermanente Piedi",
                categoria = "unghie",
                durataMinuti = 75,
                prezzo = 30.0,
                descrizione = "Applicazione smalto semipermanente con pedicure base",
                attivo = true
            ),
            Trattamento(
                id = "",
                nome = "Ricostruzione Unghie",
                categoria = "unghie",
                durataMinuti = 120,
                prezzo = 45.0,
                descrizione = "Ricostruzione unghie in gel con forma personalizzata",
                attivo = true
            ),
            Trattamento(
                id = "",
                nome = "Manicure Classica",
                categoria = "unghie",
                durataMinuti = 30,
                prezzo = 15.0,
                descrizione = "Manicure base con smalto normale",
                attivo = true
            ),

            // Massaggi
            Trattamento(
                id = "",
                nome = "Massaggio Rilassante",
                categoria = "massaggi",
                durataMinuti = 50,
                prezzo = 40.0,
                descrizione = "Massaggio completo per rilassare corpo e mente",
                attivo = true
            ),
            Trattamento(
                id = "",
                nome = "Massaggio Viso",
                categoria = "massaggi",
                durataMinuti = 30,
                prezzo = 25.0,
                descrizione = "Massaggio viso con prodotti specifici",
                attivo = true
            ),

            // Trattamenti viso
            Trattamento(
                id = "",
                nome = "Pulizia Viso",
                categoria = "viso",
                durataMinuti = 60,
                prezzo = 35.0,
                descrizione = "Pulizia profonda del viso con prodotti professionali",
                attivo = true
            ),
            Trattamento(
                id = "",
                nome = "Trattamento Anti-Age",
                categoria = "viso",
                durataMinuti = 75,
                prezzo = 50.0,
                descrizione = "Trattamento anti-età con sieri e maschere specifiche",
                attivo = true
            ),

            // Depilazione
            Trattamento(
                id = "",
                nome = "Ceretta Gambe Complete",
                categoria = "depilazione",
                durataMinuti = 45,
                prezzo = 25.0,
                descrizione = "Ceretta gambe complete",
                attivo = true
            ),
            Trattamento(
                id = "",
                nome = "Ceretta Inguine Totale",
                categoria = "depilazione",
                durataMinuti = 30,
                prezzo = 20.0,
                descrizione = "Ceretta inguine totale",
                attivo = true
            )
        )
    }

    private fun createProdotti(): List<Prodotto> {
        return listOf(
            // Smalti
            Prodotto(
                id = "",
                nome = "Smalto OPI Red",
                categoria = "smalti",
                prezzo = 12.0,
                descrizione = "Smalto rosso classico OPI",
                attivo = true
            ),
            Prodotto(
                id = "",
                nome = "Smalto Essie Nude",
                categoria = "smalti",
                prezzo = 10.0,
                descrizione = "Smalto nude elegante Essie",
                attivo = true
            ),

            // Gel
            Prodotto(
                id = "",
                nome = "Gel UV Builder Clear",
                categoria = "gel",
                prezzo = 18.0,
                descrizione = "Gel costruttore trasparente UV",
                attivo = true
            ),
            Prodotto(
                id = "",
                nome = "Gel Polish Rosa Antico",
                categoria = "gel",
                prezzo = 15.0,
                descrizione = "Smalto gel semipermanente rosa antico",
                attivo = true
            ),

            // Creme viso
            Prodotto(
                id = "",
                nome = "Crema Idratante Viso",
                categoria = "creme_viso",
                prezzo = 25.0,
                descrizione = "Crema idratante per tutti i tipi di pelle",
                attivo = true
            ),
            Prodotto(
                id = "",
                nome = "Siero Anti-Age",
                categoria = "creme_viso",
                prezzo = 35.0,
                descrizione = "Siero concentrato anti-età con acido ialuronico",
                attivo = true
            ),

            // Accessori
            Prodotto(
                id = "",
                nome = "Lima Professionale",
                categoria = "accessori",
                prezzo = 5.0,
                descrizione = "Lima per unghie professionale",
                attivo = true
            ),
            Prodotto(
                id = "",
                nome = "Kit Manicure Completo",
                categoria = "accessori",
                prezzo = 20.0,
                descrizione = "Kit completo per manicure a casa",
                attivo = true
            )
        )
    }

    private fun createAppuntamenti(dipendenti: List<User>, clienti: List<Cliente>): List<Appuntamento> {
        if (dipendenti.isEmpty() || clienti.isEmpty()) return emptyList()

        // Helper per creare data
        fun getDateForDay(daysFromNow: Int, hour: Int, minute: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, daysFromNow)
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.time
        }

        val appuntamenti = mutableListOf<Appuntamento>()

        // Questa settimana - Valentina
        if (dipendenti.isNotEmpty() && clienti.isNotEmpty()) {
            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[0].id,
                dipendentaId = dipendenti[0].id,
                data = getDateForDay(0, 9, 0),
                oraInizio = "09:00",
                oraFine = "10:30",
                trattamentoId = "tratt1",
                note = "Semipermanente mani",
                stato = "confermato",
                prezzo = 25.0
            ))

            if (clienti.size > 1) {
                appuntamenti.add(Appuntamento(
                    id = "",
                    clienteId = clienti[1].id,
                    dipendentaId = dipendenti[0].id,
                    data = getDateForDay(0, 11, 0),
                    oraInizio = "11:00",
                    oraFine = "12:00",
                    trattamentoId = "tratt2",
                    note = "",
                    stato = "confermato",
                    prezzo = 30.0
                ))
            }

            if (clienti.size > 2) {
                appuntamenti.add(Appuntamento(
                    id = "",
                    clienteId = clienti[2].id,
                    dipendentaId = dipendenti[0].id,
                    data = getDateForDay(0, 14, 0),
                    oraInizio = "14:00",
                    oraFine = "15:30",
                    trattamentoId = "tratt1",
                    note = "",
                    stato = "confermato",
                    prezzo = 25.0
                ))
            }
        }

        // Domani - Sara
        if (dipendenti.size > 1 && clienti.size > 2) {
            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[2].id,
                dipendentaId = dipendenti[1].id,
                data = getDateForDay(1, 10, 0),
                oraInizio = "10:00",
                oraFine = "11:30",
                trattamentoId = "tratt3",
                note = "Ricostruzione",
                stato = "confermato",
                prezzo = 45.0
            ))

            if (clienti.size > 3) {
                appuntamenti.add(Appuntamento(
                    id = "",
                    clienteId = clienti[3].id,
                    dipendentaId = dipendenti[1].id,
                    data = getDateForDay(1, 15, 0),
                    oraInizio = "15:00",
                    oraFine = "16:00",
                    trattamentoId = "tratt2",
                    note = "",
                    stato = "confermato",
                    prezzo = 30.0
                ))
            }
        }

        // Dopodomani - Silvia
        if (dipendenti.size > 2 && clienti.size > 4) {
            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[4].id,
                dipendentaId = dipendenti[2].id,
                data = getDateForDay(2, 9, 30),
                oraInizio = "09:30",
                oraFine = "11:00",
                trattamentoId = "tratt1",
                note = "",
                stato = "confermato",
                prezzo = 25.0
            ))

            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[0].id,
                dipendentaId = dipendenti[2].id,
                data = getDateForDay(2, 13, 0),
                oraInizio = "13:00",
                oraFine = "14:00",
                trattamentoId = "tratt4",
                note = "Pulizia viso",
                stato = "confermato",
                prezzo = 35.0
            ))
        }

        // Tra 3 giorni
        if (dipendenti.isNotEmpty() && clienti.size > 1) {
            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[1].id,
                dipendentaId = dipendenti[0].id,
                data = getDateForDay(3, 10, 0),
                oraInizio = "10:00",
                oraFine = "11:30",
                trattamentoId = "tratt1",
                note = "",
                stato = "confermato",
                prezzo = 25.0
            ))
        }

        if (dipendenti.size > 1 && clienti.size > 2) {
            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[2].id,
                dipendentaId = dipendenti[1].id,
                data = getDateForDay(3, 16, 0),
                oraInizio = "16:00",
                oraFine = "17:00",
                trattamentoId = "tratt2",
                note = "",
                stato = "confermato",
                prezzo = 30.0
            ))
        }

        // Tra 4 giorni
        if (dipendenti.size > 1 && clienti.size > 3) {
            appuntamenti.add(Appuntamento(
                id = "",
                clienteId = clienti[3].id,
                dipendentaId = dipendenti[1].id,
                data = getDateForDay(4, 9, 0),
                oraInizio = "09:00",
                oraFine = "10:30",
                trattamentoId = "tratt3",
                note = "",
                stato = "confermato",
                prezzo = 45.0
            ))

            if (clienti.size > 4) {
                appuntamenti.add(Appuntamento(
                    id = "",
                    clienteId = clienti[4].id,
                    dipendentaId = dipendenti[1].id,
                    data = getDateForDay(4, 14, 0),
                    oraInizio = "14:00",
                    oraFine = "15:30",
                    trattamentoId = "tratt1",
                    note = "",
                    stato = "confermato",
                    prezzo = 25.0
                ))
            }
        }

        return appuntamenti
    }
}

// Repository temporanei per Trattamenti e Prodotti
class FirestoreTrattamentoRepositoryBKP {
    suspend fun addTrattamento(trattamento: Trattamento): Result<String> {
        return try {
            val docRef = Firebase.firestore
                .collection("trattamenti")
                .add(trattamento)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class FirestoreProdottoRepositoryBKP {
    suspend fun addProdotto(prodotto: Prodotto): Result<String> {
        return try {
            val docRef = Firebase.firestore
                .collection("prodotti")
                .add(prodotto)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}