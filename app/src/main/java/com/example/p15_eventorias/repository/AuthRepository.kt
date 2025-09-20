package com.example.p15_eventorias.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val res = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(res.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val res = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(res.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val res = firebaseAuth.signInWithCredential(credential).await()
            Result.success(res.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun currentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun signOut() {
        firebaseAuth.signOut()
    }
}