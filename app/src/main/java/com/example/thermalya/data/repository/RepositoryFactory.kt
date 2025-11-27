// File: data/repository/RepositoryFactory.kt
package com.example.thermalya.data.repository

import android.content.Context

/**
 * Factory per creare repository basato sulla configurazione scelta
 * Ora usa Firestore come database principale
 */
object RepositoryFactory {

    enum class DatabaseType {
        FIRESTORE,
        SQLITE  // Per futuro sviluppo offline
    }

    // Usa Firestore come database predefinito
    private var currentDatabase = DatabaseType.FIRESTORE

    fun setDatabase(type: DatabaseType) {
        currentDatabase = type
    }

    fun createUserRepository(context: Context): IUserRepository {
        return when (currentDatabase) {
            DatabaseType.FIRESTORE -> {
                FirestoreUserRepository()
            }
            DatabaseType.SQLITE -> {
                // TODO: implementare SQLiteUserRepository per modalità offline
                throw NotImplementedError("SQLiteUserRepository non ancora implementato")
            }
        }
    }

    fun createClienteRepository(context: Context): IClienteRepository {
        return when (currentDatabase) {
            DatabaseType.FIRESTORE -> {
                FirestoreClienteRepository()
            }
            DatabaseType.SQLITE -> {
                // TODO: implementare SQLiteClienteRepository per modalità offline
                throw NotImplementedError("SQLiteClienteRepository non ancora implementato")
            }
        }
    }

    fun createTrattamentoRepository(context: Context): ITrattamentoRepository {
        return when (currentDatabase) {
            DatabaseType.FIRESTORE -> {
                // TODO: implementare FirestoreTrattamentoRepository
                throw NotImplementedError("FirestoreTrattamentoRepository non ancora implementato")
            }
            DatabaseType.SQLITE -> {
                // TODO: implementare SQLiteTrattamentoRepository
                throw NotImplementedError("SQLiteTrattamentoRepository non ancora implementato")
            }
        }
    }

    fun createProdottoRepository(context: Context): IProdottoRepository {
        return when (currentDatabase) {
            DatabaseType.FIRESTORE -> {
                // TODO: implementare FirestoreProdottoRepository
                throw NotImplementedError("FirestoreProdottoRepository non ancora implementato")
            }
            DatabaseType.SQLITE -> {
                // TODO: implementare SQLiteProdottoRepository
                throw NotImplementedError("SQLiteProdottoRepository non ancora implementato")
            }
        }
    }

    fun createAppuntamentoRepository(context: Context): IAppuntamentoRepository {
        return when (currentDatabase) {
            DatabaseType.FIRESTORE -> {
                // TODO: implementare FirestoreAppuntamentoRepository
                throw NotImplementedError("FirestoreAppuntamentoRepository non ancora implementato")
            }
            DatabaseType.SQLITE -> {
                // TODO: implementare SQLiteAppuntamentoRepository
                throw NotImplementedError("SQLiteAppuntamentoRepository non ancora implementato")
            }
        }
    }

    fun createTransazioneRepository(context: Context): ITransazioneRepository {
        return when (currentDatabase) {
            DatabaseType.FIRESTORE -> {
                // TODO: implementare FirestoreTransazioneRepository
                throw NotImplementedError("FirestoreTransazioneRepository non ancora implementato")
            }
            DatabaseType.SQLITE -> {
                // TODO: implementare SQLiteTransazioneRepository
                throw NotImplementedError("SQLiteTransazioneRepository non ancora implementato")
            }
        }
    }
}