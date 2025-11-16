package com.example.drivesafe.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.drivesafe.R

import com.example.drivesafe.MotionStateHolder
import com.example.drivesafe.location.LocationTracker


/**
 * Foreground service that keeps tracking speed & motion even when the app is backgrounded.
 */
class LocationService : Service() {

    private lateinit var tracker: LocationTracker

    override fun onCreate() {
        super.onCreate()

        tracker = LocationTracker(applicationContext) { speedKmh, isMoving ->
            // Update global UI-visible state
            MotionStateHolder.update(speedKmh, isMoving)

            // Optional: still log for debugging
            Log.d("LocationService", "speed=${speedKmh}km/h, moving=$isMoving")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundWithNotification()
        tracker.start()
        // START_STICKY so Android tries to recreate the service if killed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundWithNotification() {
        val channelId = "drivesafe_location_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "DriveSafe Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("DriveSafe is running")
            .setContentText("Tracking driving activity…")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // or ic_launcher
            .setOngoing(true)
            .build()

        startForeground(notificationId, notification)
    }
}
