package com.example.weather_app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

/**
 * A helper class to simplify working with device location.
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Checks if the app has permission to access the device's location.
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Attempts to get the last known location of the device.
     * Note: This requires location permissions to be granted first.
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation(): Task<Location> {
        return fusedLocationClient.lastLocation
    }
}
