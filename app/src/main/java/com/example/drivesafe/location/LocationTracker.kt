package com.example.drivesafe.location


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Handles continuous location updates and converts them to speed + motion state.
 * Assumes permissions have already been granted.
 */
class LocationTracker(
    private val context: Context,
    private val onUpdate: (speedKmh: Float, isMoving: Boolean) -> Unit
) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest =
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1_000L // request every 1 second
        )
            .setMinUpdateIntervalMillis(500L)
            .build()

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                val speedMps = location.speed              // m/s
                val speedKmh = speedMps * 3.6f             // km/h
                val isMoving = speedKmh > MOVING_THRESHOLD_KMH
                onUpdate(speedKmh, isMoving)
            }
        }
    }

    fun start() {
        // Double-check permissions to avoid crashes
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            // No permission, don't start
            return
        }

        fusedClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
    }

    fun stop() {
        fusedClient.removeLocationUpdates(callback)
    }

    companion object {
        // You can tweak this threshold to define "moving"
        private const val MOVING_THRESHOLD_KMH = 10f
    }
}
