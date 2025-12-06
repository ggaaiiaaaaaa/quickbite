package com.quickbite.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.database.AppDatabase
import com.quickbite.database.entities.BranchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FirebaseBranchRepository(
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    suspend fun syncBranchesFromFirebase() {
        try {
            val snapshot = firestore.collection("branches")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val branches = snapshot.documents.map { doc ->
                BranchEntity(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    type = doc.getString("type") ?: "",
                    address = doc.getString("address") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    operatingHours = doc.getString("operatingHours") ?: "",
                    coffeeMachineStatus = doc.getString("coffeeMachineStatus") ?: "available",
                    donutAvailability = doc.getString("donutAvailability") ?: "high",
                    isActive = doc.getBoolean("isActive") ?: true
                )
            }

            database.branchDao().insertBranches(branches)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLocalBranches(): Flow<List<BranchEntity>> {
        return database.branchDao().getAllBranches()
    }

    suspend fun getBranchById(branchId: String): BranchEntity? {
        return database.branchDao().getBranchById(branchId)
    }
}
