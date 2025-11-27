// File: data/repository/IProdottoRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.Prodotto

interface IProdottoRepository {
    suspend fun addProdotto(prodotto: Prodotto): Result<String>
    suspend fun getProdotto(prodottoId: String): Result<Prodotto?>
    suspend fun getAllProdotti(): Result<List<Prodotto>>
    suspend fun updateProdotto(prodottoId: String, prodotto: Prodotto): Result<Boolean>
    suspend fun deleteProdotto(prodottoId: String): Result<Boolean>
}
