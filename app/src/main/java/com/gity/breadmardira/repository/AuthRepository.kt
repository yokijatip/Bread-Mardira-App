package com.gity.breadmardira.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser get() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val userMap = hashMapOf("email" to email, "role" to "customer")
                FirebaseFirestore.getInstance().collection("users").document(user.uid).set(userMap).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(uid: String): String {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users").document(uid).get().await()
        return snapshot.getString("role") ?: "customer"
    }

    fun logout() = firebaseAuth.signOut()
}