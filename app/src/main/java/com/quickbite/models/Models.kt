package com.quickbite.models

import com.google.gson.annotations.SerializedName

// Authentication Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("user_id") val userId: Int = -1,
    @SerializedName("user_name") val userName: String = "",
    val email: String = ""
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)

// Branch Models
data class Branch(
    val id: Int,
    val name: String,
    val type: String, // "Kiosk", "Mall", "Drive-Thru"
    val address: String,
    val distance: Double,
    @SerializedName("operating_hours") val operatingHours: String,
    @SerializedName("coffee_machine_status") val coffeeMachineStatus: String,
    @SerializedName("donut_availability") val donutAvailability: String,
    val latitude: Double,
    val longitude: Double
)

data class BranchListResponse(
    val success: Boolean,
    val branches: List<Branch>,
    val message: String = ""
)

data class BranchDetailResponse(
    val success: Boolean,
    val branch: Branch,
    val message: String = ""
)

// Menu/Product Models
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("is_seasonal") val isSeasonal: Boolean = false,
    @SerializedName("is_fresh_today") val isFreshToday: Boolean = false,
    val calories: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val allergens: String = ""
)

data class MenuResponse(
    val success: Boolean,
    val products: List<Product>,
    val message: String = ""
)

data class ProductDetailResponse(
    val success: Boolean,
    val product: Product,
    val message: String = ""
)

// Cart Models
data class CustomizationOptions(
    val size: String = "",
    val milkType: String = "",
    val sweetness: Int = 50,
    val extraShot: Boolean = false,
    val whippedCream: Boolean = false,
    val caramelDrizzle: Boolean = false,
    val filling: String = "",
    val glaze: String = "",
    val specialInstructions: String = ""
)

data class CartItem(
    val productId: Int,
    val productName: String,
    val basePrice: Double,
    val quantity: Int,
    val customizations: CustomizationOptions,
    val totalPrice: Double,
    val imageUrl: String = ""
)

// Order Models
data class OrderRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("order_type") val orderType: String, // "Dine-In", "Take-Out", "Drive-Thru"
    @SerializedName("table_number") val tableNumber: String = "",
    @SerializedName("pickup_time") val pickupTime: String = "",
    val items: List<OrderItemRequest>,
    @SerializedName("payment_method") val paymentMethod: String,
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    @SerializedName("promo_code") val promoCode: String = "",
    @SerializedName("special_instructions") val specialInstructions: String = ""
)

data class OrderItemRequest(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int,
    val customizations: String, // JSON string of CustomizationOptions
    val price: Double
)

data class OrderSubmitResponse(
    val success: Boolean,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("order_number") val orderNumber: String,
    val message: String
)

data class OrderStatusResponse(
    val success: Boolean,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("order_number") val orderNumber: String,
    val status: String, // "Received", "Preparing", "Ready", "Completed"
    @SerializedName("estimated_time") val estimatedTime: Int,
    val message: String = ""
)

data class OrderHistoryItem(
    val id: Int,
    @SerializedName("order_number") val orderNumber: String,
    val date: String,
    val total: Double,
    val status: String,
    val items: List<OrderItemRequest>
)

data class OrderHistoryResponse(
    val success: Boolean,
    val orders: List<OrderHistoryItem>,
    val message: String = ""
)

// Promo Code Models
data class PromoCodeResponse(
    val success: Boolean,
    @SerializedName("discount_amount") val discountAmount: Double = 0.0,
    @SerializedName("discount_percentage") val discountPercentage: Int = 0,
    val message: String
)

// Rating Models
data class RatingRequest(
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("user_id") val userId: Int,
    val rating: Float,
    val review: String
)

data class RatingResponse(
    val success: Boolean,
    val message: String
)

// Profile Models
data class ProfileUpdateRequest(
    val name: String,
    val phone: String,
    val email: String
)

data class ProfileResponse(
    val success: Boolean,
    val message: String
)

data class FavoriteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("product_id") val productId: Int
)

data class FavoriteResponse(
    val success: Boolean,
    val message: String
)

data class FavoritesResponse(
    val success: Boolean,
    val favorites: List<Product>,
    val message: String = ""
)
