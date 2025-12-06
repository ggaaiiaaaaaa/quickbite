package com.quickbite.database.dao

import androidx.room.*
import com.quickbite.database.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category AND isAvailable = 1")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isSeasonal = 1 AND isAvailable = 1")
    fun getSeasonalProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' AND isAvailable = 1")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}

