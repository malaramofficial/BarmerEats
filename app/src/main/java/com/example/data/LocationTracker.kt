package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    /**
     * Obtains the last known location of the rider.
     */
    @android.annotation.SuppressLint("MissingPermission")
    fun getLastKnownLocation(onResult: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            onResult(null)
            return
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    onResult(location)
                }
                .addOnFailureListener { e ->
                    Log.e("LocationTracker", "Failed to retrieve last known location: ${e.message}")
                    onResult(null)
                }
        } catch (e: SecurityException) {
            Log.e("LocationTracker", "SecurityException during last location call: ${e.message}")
            onResult(null)
        }
    }

    /**
     * Exposes a continuous flow of location updates, throttled to prevent excess database reads/writes.
     */
    @android.annotation.SuppressLint("MissingPermission")
    fun getLocationUpdates(intervalMillis: Long = 10000): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(Exception("Location permission is missing."))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
            .setMinUpdateIntervalMillis(intervalMillis / 2)
            .setMinUpdateDistanceMeters(5.0f) // Only trigger updates if moved by 5 meters or more to save battery
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    Log.d("LocationTracker", "New throttled location recorded: Lat: ${location.latitude}, Lng: ${location.longitude}")
                    trySend(location)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            Log.d("LocationTracker", "Stopping location updates callback subscription.")
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}
