package com.quickbite.utils

import com.google.gson.GsonBuilder
import com.quickbite.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class DatabaseHelper {

    private val apiService: ApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-api-domain.com/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    // Authentication
    suspend fun loginUser(email: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(email, password))
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        phone: String
    ): RegisterResponse {
        return apiService.register(RegisterRequest(name, email, password, phone))
    }

    // Branch Operations
    suspend fun fetchBranches(): BranchListResponse {
        return apiService.getBranches()
    }

    suspend fun fetchBranchDetails(branchId: Int): BranchDetailResponse {
        return apiService.getBranchDetails(branchId)
    }

    // Menu Operations
    suspend fun fetchMenu(category: String? = null): MenuResponse {
        return apiService.getMenu(category)
    }

    suspend fun fetchSeasonalItems(): MenuResponse {
        return apiService.getSeasonalItems()
    }

    suspend fun fetchProductDetails(productId: Int): ProductDetailResponse {
        return apiService.getProductDetails(productId)
    }

    suspend fun searchProducts(query: String): MenuResponse {
        return apiService.searchProducts(query)
    }

    // Order Operations
    suspend fun submitOrder(orderDetails: OrderRequest): OrderSubmitResponse {
        return apiService.submitOrder(orderDetails)
    }

    suspend fun getOrderStatus(orderId: Int): OrderStatusResponse {
        return apiService.getOrderStatus(orderId)
    }

    suspend fun getOrderHistory(userId: Int): OrderHistoryResponse {
        return apiService.getOrderHistory(userId)
    }

    // Promo Code
    suspend fun validatePromoCode(code: String): PromoCodeResponse {
        return apiService.validatePromoCode(code)
    }

    // Rating & Review
    suspend fun submitRating(
        orderId: Int,
        userId: Int,
        rating: Float,
        review: String
    ): RatingResponse {
        return apiService.submitRating(RatingRequest(orderId, userId, rating, review))
    }

    // Profile
    suspend fun updateProfile(userId: Int, profileData: ProfileUpdateRequest): ProfileResponse {
        return apiService.updateProfile(userId, profileData)
    }

    suspend fun getFavorites(userId: Int): FavoritesResponse {
        return apiService.getFavorites(userId)
    }

    suspend fun addFavorite(userId: Int, productId: Int): FavoriteResponse {
        return apiService.addFavorite(FavoriteRequest(userId, productId))
    }
}

// Retrofit API Interface
interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("branches")
    suspend fun getBranches(): BranchListResponse

    @GET("branches/{id}")
    suspend fun getBranchDetails(@Path("id") branchId: Int): BranchDetailResponse

    @GET("menu")
    suspend fun getMenu(@Query("category") category: String?): MenuResponse

    @GET("menu/seasonal")
    suspend fun getSeasonalItems(): MenuResponse

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") productId: Int): ProductDetailResponse

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): MenuResponse

    @POST("orders")
    suspend fun submitOrder(@Body order: OrderRequest): OrderSubmitResponse

    @GET("orders/{id}/status")
    suspend fun getOrderStatus(@Path("id") orderId: Int): OrderStatusResponse

    @GET("orders/history/{userId}")
    suspend fun getOrderHistory(@Path("userId") userId: Int): OrderHistoryResponse

    @POST("promo/validate")
    suspend fun validatePromoCode(@Query("code") code: String): PromoCodeResponse

    @POST("ratings")
    suspend fun submitRating(@Body rating: RatingRequest): RatingResponse

    @PUT("users/{id}")
    suspend fun updateProfile(@Path("id") userId: Int, @Body profile: ProfileUpdateRequest): ProfileResponse

    @GET("favorites/{userId}")
    suspend fun getFavorites(@Path("userId") userId: Int): FavoritesResponse

    @POST("favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): FavoriteResponse
}