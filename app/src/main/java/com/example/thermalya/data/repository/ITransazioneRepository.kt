// File: data/repository/ITransazioneRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Transazione

interface ITransazioneRepository {
    suspend fun addTransazione(transazione: Transazione): Result<String>
    suspend fun getTransazione(transazioneId: String): Result<Transazione?>
    suspend fun getTransazioniByCliente(clienteId: String): Result<List<Transazione>>
    suspend fun getAllTransazioni(): Result<List<Transazione>>
    suspend fun updateTransazione(transazioneId: String, transazione: Transazione): Result<Boolean>
    suspend fun deleteTransazione(transazioneId: String): Result<Boolean>
}

