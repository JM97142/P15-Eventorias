package com.example.p15_eventorias.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    suspend fun createUserProfile(
        user: FirebaseUser,
        name: String,
        photoUri: Uri?
    ): Result<Unit> {
        return try {
            var photoUrl: String? = null

            // Upload photo si elle existe
            if (photoUri != null) {
                val ref = storage.reference.child("user_photos/${UUID.randomUUID()}.jpg")
                ref.putFile(photoUri).await()
                photoUrl = ref.downloadUrl.await().toString()
            }

            // Enregistrer les infos utilisateur dans Firestore
            val userData = mapOf(
                "uid" to user.uid,
                "name" to name,
                "email" to user.email,
                "photoUrl" to photoUrl
            )
            db.collection("users").document(user.uid).set(userData).await()

            Result.success(Unit)
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