// File: data/repository/FirestoreClienteRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirestoreClienteRepository : IClienteRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val clientiCollection = db.collection("clienti")

    override suspend fun addCliente(cliente: Cliente): Result<String> {
        return try {
            val clienteData = cliente.copy(
                dataRegistrazione = Date(),
                attiva = true
            )

            val docRef = if (cliente.id.isEmpty()) {
                // Crea nuovo documento con ID generato da Firestore
                clientiCollection.add(clienteData).await()
            } else {
                // Usa ID esistente
                clientiCollection.document(cliente.id).set(clienteData).await()
                clientiCollection.document(cliente.id)
            }

            val clienteId = if (cliente.id.isEmpty()) docRef.id else cliente.id
            android.util.Log.d("FirestoreClienteRepo", "Cliente aggiunto con ID: $clienteId")
            Result.success(clienteId)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore aggiunta cliente", e)
            Result.failure(e)
        }
    }

    override suspend fun getCliente(clienteId: String): Result<Cliente?> {
        return try {
            val document = clientiCollection.document(clienteId).get().await()
            val cliente = document.toObject(Cliente::class.java)?.copy(id = document.id)

            if (cliente != null) {
                android.util.Log.d("FirestoreClienteRepo", "Cliente recuperato: ${cliente.nomeCompleto()}")
            } else {
                android.util.Log.w("FirestoreClienteRepo", "Cliente non trovato: $clienteId")
            }

            Result.success(cliente)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore recupero cliente", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllClienti(): Result<List<Cliente>> {
        return try {
            val snapshot = clientiCollection
                .whereEqualTo("attiva", true)
                .orderBy("cognome", Query.Direction.ASCENDING)
                .orderBy("nome", Query.Direction.ASCENDING)
                .get()
                .await()

            val clienti = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Cliente::class.java)?.copy(id = doc.id)
            }

            android.util.Log.d("FirestoreClienteRepo", "Clienti caricate: ${clienti.size}")
            Result.success(clienti)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore caricamento clienti", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCliente(clienteId: String, cliente: Cliente): Result<Boolean> {
        return try {
            val updatedCliente = cliente.copy(id = clienteId)
            clientiCollection.document(clienteId).set(updatedCliente).await()

            android.util.Log.d("FirestoreClienteRepo", "Cliente aggiornato: $clienteId - ${cliente.nomeCompleto()}")
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore aggiornamento cliente", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCliente(clienteId: String): Result<Boolean> {
        return try {
            // Soft delete: marca come non attiva invece di eliminare
            clientiCollection.document(clienteId)
                .update("attiva", false)
                .await()

            android.util.Log.d("FirestoreClienteRepo", "Cliente disattivato: $clienteId")
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore eliminazione cliente", e)
            Result.failure(e)
        }
    }

    override suspend fun searchClienti(query: String): Result<List<Cliente>> {
        return try {
            if (query.isBlank()) {
                // Se la query è vuota, restituisci tutti i clienti
                return getAllClienti()
            }

            val queryLower = query.lowercase().trim()

            // Firestore non supporta ricerca full-text nativa
            // Dobbiamo caricare tutti i clienti e filtrare lato client
            val allClientiResult = getAllClienti()

            allClientiResult.fold(
                onSuccess = { clienti ->
                    val filtered = clienti.filter { cliente ->
                        cliente.nome.lowercase().contains(queryLower) ||
                                cliente.cognome.lowercase().contains(queryLower) ||
                                cliente.telefono.contains(queryLower) ||
                                cliente.email.lowercase().contains(queryLower)
                    }

                    android.util.Log.d("FirestoreClienteRepo", "Ricerca '$query': ${filtered.size} risultati")
                    Result.success(filtered)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore ricerca clienti", e)
            Result.failure(e)
        }
    }

    /**
     * Funzione helper per aggiornare l'ultima visita di un cliente
     */
    suspend fun updateUltimaVisita(clienteId: String, data: Date): Result<Boolean> {
        return try {
            clientiCollection.document(clienteId)
                .update("ultimaVisita", data)
                .await()

            android.util.Log.d("FirestoreClienteRepo", "Ultima visita aggiornata per cliente: $clienteId")
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore aggiornamento ultima visita", e)
            Result.failure(e)
        }
    }

    /**
     * Funzione helper per aggiornare le note di un cliente
     */
    suspend fun updateNote(clienteId: String, note: String): Result<Boolean> {
        return try {
            clientiCollection.document(clienteId)
                .update("note", note)
                .await()

            android.util.Log.d("FirestoreClienteRepo", "Note aggiornate per cliente: $clienteId")
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreClienteRepo", "Errore aggiornamento note", e)
            Result.failure(e)
        }
    }
}