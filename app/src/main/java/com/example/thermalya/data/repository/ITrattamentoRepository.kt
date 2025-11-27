// File: data/repository/ITrattamentoRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Trattamento

interface ITrattamentoRepository {
    suspend fun addTrattamento(trattamento: Trattamento): Result<String>
    suspend fun getTrattamento(trattamentoId: String): Result<Trattamento?>
    suspend fun getAllTrattamenti(): Result<List<Trattamento>>
    suspend fun updateTrattamento(trattamentoId: String, trattamento: Trattamento): Result<Boolean>
    suspend fun deleteTrattamento(trattamentoId: String): Result<Boolean>
}