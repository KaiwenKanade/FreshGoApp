package com.example.freshgoapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "expiry_alerts_channel"
    private const val CHANNEL_NAME = "Peringatan Kadaluarsa FreshGo"

    fun showExpiryNotification(context: Context, itemName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Mengingatkan pengguna saat bahan makanan hampir kadaluarsa"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Membangun tampilan notifikasi
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Menggunakan ikon sistem default bawaan Android
            .setContentTitle("⚠️ Bahan Hampir Kadaluarsa!")
            .setContentText("Stok bahan '$itemName' Anda sudah mendekati tanggal kadaluarsa. Yuk segera dimasak!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()


        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}