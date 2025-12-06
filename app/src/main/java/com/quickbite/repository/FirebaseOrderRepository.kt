package com.quickbite.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.database.AppDatabase
import com.quickbite.database.entities.OrderEntity
import com.quickbite.database.entities.OrderItemEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseOrderRepository(
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    suspend fun submitOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ): Result<String> {
        return try {
            val orderId = UUID.randomUUID().toString()
            val orderNumber = "QB${System.currentTimeMillis().toString().takeLast(6)}"

            val orderMap = hashMapOf(
                "orderNumber" to orderNumber,
                "userId" to order.userId,
                "branchId" to order.branchId,
                "branchName" to order.branchName,
                "orderType" to order.orderType,
                "tableNumber" to order.tableNumber,
                "pickupTime" to order.pickupTime,
                "status" to "Pending",
                "paymentMethod" to order.paymentMethod,
                "subtotal" to order.subtotal,
                "discount" to order.discount,
                "total" to order.total,
                "promoCode" to order.promoCode,
                "specialInstructions" to order.specialInstructions,
                "estimatedTime" to 15,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            // Save order to Firebase
            firestore.collection("orders").document(orderId).set(orderMap).await()

            // Save order items to Firebase
            val itemsMap = items.map { item ->
                hashMapOf(
                    "productId" to item.productId,
                    "productName" to item.productName,
                    "productImageUrl" to item.productImageUrl,
                    "quantity" to item.quantity,
                    "customizations" to item.customizations,
                    "price" to item.price
                )
            }

            firestore.collection("orders").document(orderId)
                .collection("items")
                .document("items")
                .set(hashMapOf("items" to itemsMap))
                .await()

            // Save to local SQLite
            val localOrder = order.copy(id = orderId, orderNumber = orderNumber, status = "Pending")
            database.orderDao().insertOrder(localOrder)

            val localItems = items.map { it.copy(orderId = orderId) }
            database.orderDao().insertOrderItems(localItems)

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Real-time order status tracking
    fun observeOrderStatus(orderId: String): Flow<OrderEntity?> = callbackFlow {
        val listener = firestore.collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let { doc ->
                    if (doc.exists()) {
                        val order = OrderEntity(
                            id = doc.id,
                            orderNumber = doc.getString("orderNumber") ?: "",
                            userId = doc.getString("userId") ?: "",
                            branchId = doc.getString("branchId") ?: "",
                            branchName = doc.getString("branchName") ?: "",
                            orderType = doc.getString("orderType") ?: "",
                            tableNumber = doc.getString("tableNumber") ?: "",
                            pickupTime = doc.getString("pickupTime") ?: "",
                            status = doc.getString("status") ?: "Pending",
                            paymentMethod = doc.getString("paymentMethod") ?: "",
                            subtotal = doc.getDouble("subtotal") ?: 0.0,
                            discount = doc.getDouble("discount") ?: 0.0,
                            total = doc.getDouble("total") ?: 0.0,
                            promoCode = doc.getString("promoCode") ?: "",
                            specialInstructions = doc.getString("specialInstructions") ?: "",
                            estimatedTime = doc.getLong("estimatedTime")?.toInt() ?: 15,
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                        trySend(order)
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    fun getUserOrders(userId: String): Flow<List<OrderEntity>> {
        return database.orderDao().getUserOrders(userId)
    }

    suspend fun getOrderById(orderId: String): OrderEntity? {
        return database.orderDao().getOrderById(orderId)
    }

    suspend fun getOrderItems(orderId: String): List<OrderItemEntity> {
        return database.orderDao().getOrderItems(orderId)
    }

    fun getActiveOrders(userId: String): Flow<List<OrderEntity>> {
        return database.orderDao().getActiveOrders(userId)
    }
}
