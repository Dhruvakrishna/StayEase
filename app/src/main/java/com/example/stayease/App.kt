package com.example.stayease

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.stayease.worker.WorkScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // OSM Configuration
        Configuration.getInstance().userAgentValue = packageName
        
        // Schedule Workers
        WorkScheduler.schedule(this)
        
        // Create Notification Channel
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Booking Updates"
            val descriptionText = "Notifications about your stay reservations"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("booking_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
