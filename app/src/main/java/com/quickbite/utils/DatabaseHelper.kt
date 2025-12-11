package com.quickbite.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quickbite.database.AppDatabase
import com.quickbite.models.*
import com.quickbite.repository.*
import kotlinx.coroutines.tasks.await

class DatabaseHelper {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var productRepo: FirebaseProductRepository? = null
    private var branchRepo: FirebaseBranchRepository? = null
    private var orderRepo: FirebaseOrderRepository? = null

    fun initialize(database: AppDatabase) {
        productRepo = FirebaseProductRepository(firestore, database)
        branchRepo = FirebaseBranchRepository(firestore, database)
        orderRepo = FirebaseOrderRepository(firestore, database)
    }

    // ============ BRANCH OPERATIONS ============

    suspend fun fetchBranches(): BranchListResponse {
        return try {
            branchRepo?.syncBranchesFromFirebase()

            val snapshot = firestore.collection("branches")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val branches = snapshot.documents.map { doc ->
                Branch(
                    id = doc.id.toIntOrNull() ?: 0,
                    name = doc.getString("name") ?: "",
                    type = doc.getString("type") ?: "",
                    address = doc.getString("address") ?: "",
                    distance = doc.getDouble("distance") ?: 0.0,
                    operatingHours = doc.getString("operatingHours") ?: "",
                    coffeeMachineStatus = doc.getString("coffeeMachineStatus") ?: "available",
                    donutAvailability = doc.getString("donutAvailability") ?: "high",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0
                )
            }

            BranchListResponse(success = true, branches = branches)
        } catch (e: Exception) {
            BranchListResponse(
                success = false,
                branches = emptyList(),
                message = e.message ?: "Failed to fetch branches"
            )
        }
    }

    // ============ PRODUCT OPERATIONS ============

    suspend fun fetchProducts(): MenuResponse {
        return try {
            productRepo?.syncProductsFromFirebase()

            val snapshot = firestore.collection("products")
                .whereEqualTo("isAvailable", true)
                .get()
                .await()

            val products = snapshot.documents.map { doc ->
                MenuProduct(
                    id = doc.id.toIntOrNull() ?: 0,
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
                    allergens = doc.getString("allergens") ?: ""
                )
            }

            MenuResponse(success = true, products = products)
        } catch (e: Exception) {
            MenuResponse(
                success = false,
                products = emptyList(),
                message = e.message ?: "Failed to fetch products"
            )
        }
    }

    suspend fun fetchProductDetails(productId: Int): ProductDetailResponse {
        return try {
            val doc = firestore.collection("products")
                .document(productId.toString())
                .get()
                .await()

            if (doc.exists()) {
                val product = MenuProduct(
                    id = productId,
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
                    allergens = doc.getString("allergens") ?: ""
                )
                ProductDetailResponse(success = true, product = product)
            } else {
                ProductDetailResponse(
                    success = false,
                    product = MenuProduct(0, "", "", 0.0, "", ""),
                    message = "Product not found"
                )
            }
        } catch (e: Exception) {
            ProductDetailResponse(
                success = false,
                product = MenuProduct(0, "", "", 0.0, "", ""),
                message = e.message ?: "Failed to fetch product"
            )
        }
    }

    // ============ ORDER OPERATIONS ============

    suspend fun submitOrder(orderRequest: OrderRequest): OrderSubmitResponse {
        return try {
            val orderId = System.currentTimeMillis().toString()
            val orderNumber = "QB${orderId.takeLast(6)}"

            val orderMap = hashMapOf(
                "orderNumber" to orderNumber,
                "userId" to orderRequest.userId.toString(),
                "branchId" to orderRequest.branchId.toString(),
                "orderType" to orderRequest.orderType,
                "tableNumber" to orderRequest.tableNumber,
                "pickupTime" to orderRequest.pickupTime,
                "status" to "Pending",
                "paymentMethod" to orderRequest.paymentMethod,
                "subtotal" to orderRequest.subtotal,
                "discount" to orderRequest.discount,
                "total" to orderRequest.total,
                "promoCode" to orderRequest.promoCode,
                "specialInstructions" to orderRequest.specialInstructions,
                "estimatedTime" to 15,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("orders")
                .document(orderId)
                .set(orderMap)
                .await()

            val itemsMap = orderRequest.items.map { item ->
                hashMapOf(
                    "productId" to item.productId.toString(),
                    "quantity" to item.quantity,
                    "customizations" to item.customizations,
                    "price" to item.price
                )
            }

            firestore.collection("orders")
                .document(orderId)
                .collection("items")
                .document("items")
                .set(hashMapOf("items" to itemsMap))
                .await()

            OrderSubmitResponse(
                success = true,
                orderId = orderId.toIntOrNull() ?: 0,
                orderNumber = orderNumber,
                message = "Order placed successfully"
            )
        } catch (e: Exception) {
            OrderSubmitResponse(
                success = false,
                orderId = -1,
                orderNumber = "",
                message = e.message ?: "Failed to submit order"
            )
        }
    }

    suspend fun getOrderStatus(orderId: Int): OrderStatusResponse {
        return try {
            val doc = firestore.collection("orders")
                .document(orderId.toString())
                .get()
                .await()

            if (doc.exists()) {
                OrderStatusResponse(
                    success = true,
                    orderId = orderId,
                    orderNumber = doc.getString("orderNumber") ?: "",
                    status = doc.getString("status") ?: "Pending",
                    estimatedTime = doc.getLong("estimatedTime")?.toInt() ?: 15
                )
            } else {
                OrderStatusResponse(
                    success = false,
                    orderId = orderId,
                    orderNumber = "",
                    status = "",
                    estimatedTime = 0,
                    message = "Order not found"
                )
            }
        } catch (e: Exception) {
            OrderStatusResponse(
                success = false,
                orderId = orderId,
                orderNumber = "",
                status = "",
                estimatedTime = 0,
                message = e.message ?: "Failed to fetch order status"
            )
        }
    }

    suspend fun getOrderHistory(userId: Int): OrderHistoryResponse {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId.toString())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.map { doc ->
                OrderHistoryItem(
                    id = doc.id.toIntOrNull() ?: 0,
                    orderNumber = doc.getString("orderNumber") ?: "",
                    date = formatTimestamp(doc.getLong("createdAt") ?: 0),
                    total = doc.getDouble("total") ?: 0.0,
                    status = doc.getString("status") ?: "",
                    items = emptyList()
                )
            }

            OrderHistoryResponse(success = true, orders = orders)
        } catch (e: Exception) {
            OrderHistoryResponse(
                success = false,
                orders = emptyList(),
                message = e.message ?: "Failed to fetch order history"
            )
        }
    }

    // ============ USER OPERATIONS ============

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        phone: String
    ): RegisterResponse {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("Failed to create user")

            val userMap = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "role" to "customer",
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .set(userMap)
                .await()

            RegisterResponse(success = true, message = "Registration successful")
        } catch (e: Exception) {
            RegisterResponse(success = false, message = e.message ?: "Registration failed")
        }
    }

    // ============ RATING OPERATIONS ============

    suspend fun submitRating(
        orderId: Int,
        userId: Int,
        rating: Float,
        review: String
    ): RatingResponse {
        return try {
            val ratingMap = hashMapOf(
                "orderId" to orderId.toString(),
                "userId" to userId.toString(),
                "rating" to rating,
                "review" to review,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("ratings")
                .add(ratingMap)
                .await()

            RatingResponse(success = true, message = "Rating submitted successfully")
        } catch (e: Exception) {
            RatingResponse(success = false, message = e.message ?: "Failed to submit rating")
        }
    }

    // ============ HELPER FUNCTIONS ============

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}