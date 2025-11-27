package com.example.thermalya.data.repository

import com.example.thermalya.data.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // === UTENTI ===
    suspend fun addUser(userId: String, user: User): Result<Boolean> = try {
        db.collection("users").document(userId).set(user).await()
        Result.success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getUser(userId: String): Result<User?> = try {
        val user = db.collection("users").document(userId).get().await().toObject(User::class.java)
        Result.success(user)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAllDipendenti(): Result<List<User>> = try {
        val dipendenti = db.collection("users")
            .whereEqualTo("ruolo", "dipendente")
            .whereEqualTo("attiva", true)
            .get().await()
            .toObjects(User::class.java)
        Result.success(dipendenti)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // === CLIENTI ===
    suspend fun addCliente(cliente: Cliente): Result<String> = try {
        val docRef = db.collection("clienti").add(cliente).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getCliente(clienteId: String): Result<Cliente?> = try {
        val cliente = db.collection("clienti").document(clienteId).get().await().toObject(Cliente::class.java)
        Result.success(cliente)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAllClienti(): Result<List<Cliente>> = try {
        val clienti = db.collection("clienti")
            .whereEqualTo("attiva", true)
            .orderBy("cognome")
            .get().await()
            .toObjects(Cliente::class.java)
        Result.success(clienti)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun updateCliente(clienteId: String, cliente: Cliente): Result<Boolean> = try {
        db.collection("clienti").document(clienteId).set(cliente).await()
        Result.success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun deleteCliente(clienteId: String): Result<Boolean> = try {
        db.collection("clienti").document(clienteId).update("attiva", false).await()
        Result.success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // === TRATTAMENTI ===
    suspend fun addTrattamento(trattamento: Trattamento): Result<String> = try {
        val docRef = db.collection("trattamenti").add(trattamento).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAllTrattamenti(): Result<List<Trattamento>> = try {
        val trattamenti = db.collection("trattamenti")
            .whereEqualTo("attivo", true)
            .get().await()
            .toObjects(Trattamento::class.java)
        Result.success(trattamenti)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // === PRODOTTI ===
    suspend fun addProdotto(prodotto: Prodotto): Result<String> = try {
        val docRef = db.collection("prodotti").add(prodotto).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAllProdotti(): Result<List<Prodotto>> = try {
        val prodotti = db.collection("prodotti")
            .whereEqualTo("attivo", true)
            .get().await()
            .toObjects(Prodotto::class.java)
        Result.success(prodotti)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // === APPUNTAMENTI ===
    suspend fun addAppuntamento(appuntamento: Appuntamento): Result<String> = try {
        val docRef = db.collection("appuntamenti").add(appuntamento).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAppuntamentiByDipendente(dipendentaId: String): Result<List<Appuntamento>> = try {
        val appuntamenti = db.collection("appuntamenti")
            .whereEqualTo("dipendentaId", dipendentaId)
            .whereNotEqualTo("stato", "cancellato")
            .orderBy("stato")
            .orderBy("data")
            .get().await()
            .toObjects(Appuntamento::class.java)
        Result.success(appuntamenti)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAppuntamentiByCliente(clienteId: String): Result<List<Appuntamento>> = try {
        val appuntamenti = db.collection("appuntamenti")
            .whereEqualTo("clienteId", clienteId)
            .whereNotEqualTo("stato", "cancellato")
            .orderBy("data")
            .get().await()
            .toObjects(Appuntamento::class.java)
        Result.success(appuntamenti)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun updateAppuntamento(appuntamentoId: String, appuntamento: Appuntamento): Result<Boolean> = try {
        db.collection("appuntamenti").document(appuntamentoId).set(appuntamento).await()
        Result.success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // === TRANSAZIONI ===
    suspend fun addTransazione(transazione: Transazione): Result<String> = try {
        val docRef = db.collection("transazioni").add(transazione).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getTransazioniByCliente(clienteId: String): Result<List<Transazione>> = try {
        val transazioni = db.collection("transazioni")
            .whereEqualTo("clienteId", clienteId)
            .orderBy("data", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Transazione::class.java)
        Result.success(transazioni)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getAllTransazioni(): Result<List<Transazione>> = try {
        val transazioni = db.collection("transazioni")
            .orderBy("data", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Transazione::class.java)
        Result.success(transazioni)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}