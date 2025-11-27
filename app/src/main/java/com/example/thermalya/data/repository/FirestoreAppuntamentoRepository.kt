// File: data/repository/FirestoreAppuntamentoRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Appuntamento
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDate
import java.util.*
import java.util.Calendar

class FirestoreAppuntamentoRepository : IAppuntamentoRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val appuntamentiCollection = db.collection("appuntamenti")

    override suspend fun addAppuntamento(appuntamento: Appuntamento): Result<String> {
        return try {
            val docRef = if (appuntamento.id.isEmpty()) {
                appuntamentiCollection.add(appuntamento).await()
            } else {
                appuntamentiCollection.document(appuntamento.id).set(appuntamento).await()
                appuntamentiCollection.document(appuntamento.id)
            }

            val appuntamentoId = if (appuntamento.id.isEmpty()) docRef.id else appuntamento.id
            android.util.Log.d("FirestoreAppRepo", "Appuntamento aggiunto: $appuntamentoId")
            Result.success(appuntamentoId)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore aggiunta appuntamento", e)
            Result.failure(e)
        }
    }

    override suspend fun getAppuntamento(appuntamentoId: String): Result<Appuntamento?> {
        return try {
            val document = appuntamentiCollection.document(appuntamentoId).get().await()
            val appuntamento = document.toObject(Appuntamento::class.java)?.copy(id = document.id)
            Result.success(appuntamento)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore recupero appuntamento", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllAppuntamenti(): Result<List<Appuntamento>> {
        return try {
            val snapshot = appuntamentiCollection
                .whereNotEqualTo("stato", "cancellato")
                .orderBy("data", Query.Direction.ASCENDING)
                .get()
                .await()

            val appuntamenti = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Appuntamento::class.java)?.copy(id = doc.id)
            }

            Result.success(appuntamenti)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore caricamento appuntamenti", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAppuntamento(
        appuntamentoId: String,
        appuntamento: Appuntamento
    ): Result<Boolean> {
        return try {
            appuntamentiCollection.document(appuntamentoId)
                .set(appuntamento.copy(id = appuntamentoId))
                .await()
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore aggiornamento appuntamento", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAppuntamento(appuntamentoId: String): Result<Boolean> {
        return try {
            // Soft delete - cambia stato a "cancellato"
            appuntamentiCollection.document(appuntamentoId)
                .update("stato", "cancellato")
                .await()
            Result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore eliminazione appuntamento", e)
            Result.failure(e)
        }
    }

    override suspend fun getAppuntamentiByDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        dipendenteIds: List<String>?
    ): Result<List<Appuntamento>> {
        return try {
            // Converti org.threeten.bp.LocalDate in java.util.Date
            val startTimestamp = localDateToDate(startDate)
            val endTimestamp = localDateToDate(endDate.plusDays(1))

            var query = appuntamentiCollection
                .whereNotEqualTo("stato", "cancellato")
                .whereGreaterThanOrEqualTo("data", startTimestamp)
                .whereLessThan("data", endTimestamp)

            // Filtra per dipendenti se specificato
            if (!dipendenteIds.isNullOrEmpty()) {
                query = query.whereIn("dipendentaId", dipendenteIds)
            }

            val snapshot = query.orderBy("data", Query.Direction.ASCENDING).get().await()

            val appuntamenti = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Appuntamento::class.java)?.copy(id = doc.id)
            }

            android.util.Log.d("FirestoreAppRepo", "Appuntamenti trovati: ${appuntamenti.size} (${startDate} - ${endDate})")
            Result.success(appuntamenti)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore ricerca appuntamenti per date", e)
            Result.failure(e)
        }
    }

    override suspend fun getAppuntamentiByDipendente(
        dipendenteId: String,
        date: LocalDate
    ): Result<List<Appuntamento>> {
        return try {
            val startTimestamp = localDateToDate(date)
            val endTimestamp = localDateToDate(date.plusDays(1))

            val snapshot = appuntamentiCollection
                .whereEqualTo("dipendentaId", dipendenteId)
                .whereNotEqualTo("stato", "cancellato")
                .whereGreaterThanOrEqualTo("data", startTimestamp)
                .whereLessThan("data", endTimestamp)
                .orderBy("oraInizio", Query.Direction.ASCENDING)
                .get()
                .await()

            val appuntamenti = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Appuntamento::class.java)?.copy(id = doc.id)
            }

            Result.success(appuntamenti)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreAppRepo", "Errore ricerca appuntamenti dipendente", e)
            Result.failure(e)
        }
    }

    // Helper per convertire org.threeten.bp.LocalDate in java.util.Date
    private fun localDateToDate(localDate: LocalDate): Date {
        val calendar = Calendar.getInstance()
        calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}