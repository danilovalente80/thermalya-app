// File: data/repository/IUserRepository.kt
package com.example.thermalya.data.repository

import com.example.thermalya.data.models.User

interface IUserRepository {
    suspend fun addUser(user: User): Result<String>
    suspend fun getUser(userId: String): Result<User?>
    suspend fun getAllDipendenti(): Result<List<User>>
    suspend fun updateUser(userId: String, user: User): Result<Boolean>
    suspend fun deleteUser(userId: String): Result<Boolean>
}