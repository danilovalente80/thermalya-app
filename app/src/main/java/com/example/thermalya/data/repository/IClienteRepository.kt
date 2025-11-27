// File: data/repository/IClienteRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Cliente

interface IClienteRepository {
    suspend fun addCliente(cliente: Cliente): Result<String>
    suspend fun getCliente(clienteId: String): Result<Cliente?>
    suspend fun getAllClienti(): Result<List<Cliente>>
    suspend fun updateCliente(clienteId: String, cliente: Cliente): Result<Boolean>
    suspend fun deleteCliente(clienteId: String): Result<Boolean>
    suspend fun searchClienti(query: String): Result<List<Cliente>>
}
