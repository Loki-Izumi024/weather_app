package com.example.weatherapp.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(
    private val activity: Activity
) {

    private val client =
        LocationServices.getFusedLocationProviderClient(activity)

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1001
        )
    }

    fun getLocation(
        onSuccess: (Double, Double) -> Unit,
        onError: (Exception) -> Unit
    ) {

        if (!hasPermission()) {
            onError(SecurityException("Location permission denied"))
            return
        }

        try {
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
                .addOnSuccessListener { location ->

                    if (location != null) {
                        onSuccess(
                            location.latitude,
                            location.longitude
                        )
                    } else {
                        onError(
                            Exception("Unable to determine current location")
                        )
                    }
                }
                .addOnFailureListener {
                    onError(it)
                }

        } catch (e: SecurityException) {
            onError(e)
        }
    }
}