// File: data/repository/IAuthRepository.kt
package com.example.thermalya.data.repository

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun signup(email: String, password: String): Result<Boolean>
    fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): String?
}
