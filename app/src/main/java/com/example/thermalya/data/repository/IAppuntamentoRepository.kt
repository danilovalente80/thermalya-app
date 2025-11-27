// File: data/repository/IAppuntamentoRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Appuntamento
import org.threeten.bp.LocalDate

interface IAppuntamentoRepository {
    suspend fun addAppuntamento(appuntamento: Appuntamento): Result<String>
    suspend fun getAppuntamento(appuntamentoId: String): Result<Appuntamento?>
    suspend fun getAllAppuntamenti(): Result<List<Appuntamento>>
    suspend fun updateAppuntamento(appuntamentoId: String, appuntamento: Appuntamento): Result<Boolean>
    suspend fun deleteAppuntamento(appuntamentoId: String): Result<Boolean>
    suspend fun getAppuntamentiByDateRange(startDate: LocalDate, endDate: LocalDate, dipendenteIds: List<String>? = null): Result<List<Appuntamento>>
    suspend fun getAppuntamentiByDipendente(dipendenteId: String, date: LocalDate): Result<List<Appuntamento>>
}
