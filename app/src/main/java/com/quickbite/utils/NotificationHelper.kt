package com.quickbite.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.quickbite.R

class NotificationHelper(private val context: Context) {

    private val channelId = "quickbite_orders"
    private val channelName = "Order Updates"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for order status updates"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showOrderStatusNotification(orderNumber: String, status: String) {
        val title = when (status) {
            "Preparing" -> "Your order is being prepared"
            "Ready" -> "Your order is ready!"
            "Completed" -> "Order completed"
            else -> "Order update"
        }

        val message = "Order $orderNumber - $status"

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(orderNumber.hashCode(), notification)
        } catch (e: SecurityException) {
            // Handle permission not granted
        }
    }
}