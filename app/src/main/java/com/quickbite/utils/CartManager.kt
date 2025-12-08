package com.quickbite.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages promo codes and discounts
 * Stores active promo per user session
 */
class PromoManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "promo_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_PROMO_CODE = "promo_code"
        private const val KEY_DISCOUNT_AMOUNT = "discount_amount"
        private const val KEY_DISCOUNT_TYPE = "discount_type" // "fixed" or "percentage"
        private const val KEY_USER_ID = "user_id"

        // Predefined promo codes (in production, fetch from Firebase)
        private val VALID_PROMOS = mapOf(
            "WELCOME10" to Promo("WELCOME10", 10.0, "percentage"),
            "SAVE50" to Promo("SAVE50", 50.0, "fixed"),
            "FREEDELIVERY" to Promo("FREEDELIVERY", 0.0, "fixed"),
            "BOGO" to Promo("BOGO", 50.0, "percentage"),
            "STUDENT15" to Promo("STUDENT15", 15.0, "percentage")
        )
    }

    /**
     * Apply promo code
     */
    fun applyPromo(userId: String, code: String, subtotal: Double): PromoResult {
        val upperCode = code.uppercase().trim()
        val promo = VALID_PROMOS[upperCode]

        return if (promo != null) {
            val discount = when (promo.type) {
                "percentage" -> subtotal * (promo.value / 100.0)
                "fixed" -> promo.value
                else -> 0.0
            }

            // Save promo
            prefs.edit().apply {
                putString(KEY_PROMO_CODE, promo.code)
                putFloat(KEY_DISCOUNT_AMOUNT, discount.toFloat())
                putString(KEY_DISCOUNT_TYPE, promo.type)
                putString(KEY_USER_ID, userId)
                apply()
            }

            PromoResult.Success(promo.code, discount)
        } else {
            PromoResult.Error("Invalid promo code")
        }
    }

    /**
     * Get current active promo
     */
    fun getActivePromo(userId: String): ActivePromo? {
        val savedUserId = prefs.getString(KEY_USER_ID, null)
        if (savedUserId != userId) {
            // Different user, clear promo
            clearPromo()
            return null
        }

        val code = prefs.getString(KEY_PROMO_CODE, null)
        val discount = prefs.getFloat(KEY_DISCOUNT_AMOUNT, 0f).toDouble()
        val type = prefs.getString(KEY_DISCOUNT_TYPE, null)

        return if (code != null && type != null) {
            ActivePromo(code, discount, type)
        } else {
            null
        }
    }

    /**
     * Calculate discount for current subtotal
     */
    fun calculateDiscount(userId: String, subtotal: Double): Double {
        val activePromo = getActivePromo(userId) ?: return 0.0

        return when (activePromo.type) {
            "percentage" -> {
                // Recalculate based on current subtotal
                val promo = VALID_PROMOS[activePromo.code]
                if (promo != null) {
                    subtotal * (promo.value / 100.0)
                } else {
                    activePromo.discount // Use saved discount
                }
            }
            "fixed" -> activePromo.discount
            else -> 0.0
        }
    }

    /**
     * Remove promo
     */
    fun clearPromo() {
        prefs.edit().clear().apply()
    }

    /**
     * Check if promo is active
     */
    fun hasActivePromo(userId: String): Boolean {
        return getActivePromo(userId) != null
    }

    /**
     * Get all available promo codes (for display)
     */
    fun getAvailablePromos(): List<PromoInfo> {
        return VALID_PROMOS.values.map { promo ->
            PromoInfo(
                code = promo.code,
                description = getPromoDescription(promo),
                value = promo.value,
                type = promo.type
            )
        }
    }

    private fun getPromoDescription(promo: Promo): String {
        return when (promo.type) {
            "percentage" -> "Get ${promo.value.toInt()}% off your order"
            "fixed" -> "Get ₱${promo.value.toInt()} off your order"
            else -> "Special discount"
        }
    }
}

/**
 * Data classes
 */
data class Promo(
    val code: String,
    val value: Double,
    val type: String // "fixed" or "percentage"
)

data class ActivePromo(
    val code: String,
    val discount: Double,
    val type: String
)

data class PromoInfo(
    val code: String,
    val description: String,
    val value: Double,
    val type: String
)

sealed class PromoResult {
    data class Success(val code: String, val discount: Double) : PromoResult()
    data class Error(val message: String) : PromoResult()
}