// In C:/Users/LEGION/AndroidStudioProjects/App/app/src/main/java/com/quickbite/repository/FirebaseAuthRepository.kt

package com.quickbite.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.database.AppDatabase
import kotlinx.coroutines.tasks.await // Make sure this import is present

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val appDatabase: AppDatabase
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

    // Add this function
    suspend fun signInAnonymously(): Result<Unit> {
        return try {
            firebaseAuth.signInAnonymously().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
