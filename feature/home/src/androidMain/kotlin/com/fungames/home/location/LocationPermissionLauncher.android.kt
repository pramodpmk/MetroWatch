package com.fungames.home.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
actual fun rememberLocationPermissionLauncher(
    onLocation: (lat: Double, lon: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch { fetchLocation(context, onLocation, onDenied) }
        } else {
            onDenied()
        }
    }

    return remember(context) {
        {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                scope.launch { fetchLocation(context, onLocation, onDenied) }
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

private suspend fun fetchLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit,
    onError: () -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val provider = when {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> {
            onError()
            return
        }
    }

    try {
        @Suppress("MissingPermission")
        val lastKnown: Location? = locationManager.getLastKnownLocation(provider)
        if (lastKnown != null) {
            onLocation(lastKnown.latitude, lastKnown.longitude)
            return
        }

        suspendCancellableCoroutine { cont ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)
                    if (cont.isActive) cont.resume(Unit)
                    onLocation(location.latitude, location.longitude)
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
            }

            @Suppress("MissingPermission", "DEPRECATION")
            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())

            cont.invokeOnCancellation {
                locationManager.removeUpdates(listener)
            }
        }
    } catch (_: Exception) {
        onError()
    }
}
