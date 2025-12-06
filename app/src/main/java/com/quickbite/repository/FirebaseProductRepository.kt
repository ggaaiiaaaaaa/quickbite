package com.quickbite.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quickbite.database.AppDatabase
import com.quickbite.database.entities.ProductEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseProductRepository(
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    // Sync products from Firebase to local SQLite
    suspend fun syncProductsFromFirebase() {
        try {
            val snapshot = firestore.collection("products")
                .whereEqualTo("isAvailable", true)
                .get()
                .await()

            val products = snapshot.documents.map { doc ->
                ProductEntity(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    price = doc.getDouble("price") ?: 0.0,
                    category = doc.getString("category") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    isSeasonal = doc.getBoolean("isSeasonal") ?: false,
                    isFreshToday = doc.getBoolean("isFreshToday") ?: false,
                    calories = doc.getLong("calories")?.toInt() ?: 0,
                    fat = doc.getLong("fat")?.toInt() ?: 0,
                    carbs = doc.getLong("carbs")?.toInt() ?: 0,
                    protein = doc.getLong("protein")?.toInt() ?: 0,
                    allergens = doc.getString("allergens") ?: "",
                    isAvailable = doc.getBoolean("isAvailable") ?: true
                )
            }

            database.productDao().insertProducts(products)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Real-time listener for products
    fun observeProducts(): Flow<List<ProductEntity>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("isAvailable", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val products = it.documents.map { doc ->
                        ProductEntity(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            category = doc.getString("category") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            isSeasonal = doc.getBoolean("isSeasonal") ?: false,
                            isFreshToday = doc.getBoolean("isFreshToday") ?: false,
                            calories = doc.getLong("calories")?.toInt() ?: 0,
                            fat = doc.getLong("fat")?.toInt() ?: 0,
                            carbs = doc.getLong("carbs")?.toInt() ?: 0,
                            protein = doc.getLong("protein")?.toInt() ?: 0,
                            allergens = doc.getString("allergens") ?: "",
                            isAvailable = doc.getBoolean("isAvailable") ?: true
                        )
                    }
                    trySend(products)
                }
            }

        awaitClose { listener.remove() }
    }

    // Get products from local SQLite (offline support)
    fun getLocalProducts(): Flow<List<ProductEntity>> {
        return database.productDao().getAllProducts()
    }

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
        return database.productDao().getProductsByCategory(category)
    }

    fun getSeasonalProducts(): Flow<List<ProductEntity>> {
        return database.productDao().getSeasonalProducts()
    }

    suspend fun getProductById(productId: String): ProductEntity? {
        return database.productDao().getProductById(productId)
    }

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return database.productDao().searchProducts(query)
    }
}
