package com.quickbite.database.dao

import androidx.room.*
import com.quickbite.database.entities.OrderEntity
import com.quickbite.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserOrders(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE status IN ('Pending', 'Preparing', 'Ready') AND userId = :userId")
    fun getActiveOrders(userId: String): Flow<List<OrderEntity>>
}
