package com.example.thermalya.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    suspend fun login(email: String, password: String): Result<Boolean> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Result.success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun signup(email: String, password: String): Result<Boolean> = try {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        Result.success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null
}
