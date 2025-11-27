// File: data/repository/FirestoreUserRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository : IUserRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val usersCollection = db.collection("users")

    override suspend fun addUser(user: User): Result<String> {
        return try {
            val docRef = if (user.id.isEmpty()) {
                // Crea nuovo documento con ID generato da Firestore
                usersCollection.add(user).await()
            } else {
                // Usa ID esistente
                usersCollection.document(user.id).set(user).await()
                usersCollection.document(user.id)
            }

            val userId = if (user.id.isEmpty()) docRef.id else user.id
            android.util.Log.d("FirestoreRepo", "User aggiunto con ID: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Errore aggiunta user", e)
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)?.copy(id = document.id)
            android.util.Log.d("FirestoreRepo", "User recuperato: ${user?.nome}")
            Result.success(user)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Errore recupero user", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllDipendenti(): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("attiva", true)
                .whereIn("ruolo", listOf("dipendente", "admin"))
                .get()
                .await()

            val dipendenti = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }

            android.util.Log.d("FirestoreRepo", "Dipendenti caricate: ${dipendenti.size}")
            Result.success(dipendenti)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Errore caricamento dipendenti", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUser(userId: String, user: User): Result<Boolean> {
        return try {
            usersCollection.document(userId).set(user.copy(id = userId)).await()
            android.util.Log.d("FirestoreRepo", "User aggiornato: $userId")
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Errore aggiornamento user", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Boolean> {
        return try {
            usersCollection.document(userId).delete().await()
            android.util.Log.d("FirestoreRepo", "User eliminato: $userId")
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Errore eliminazione user", e)
            Result.failure(e)
        }
    }
}