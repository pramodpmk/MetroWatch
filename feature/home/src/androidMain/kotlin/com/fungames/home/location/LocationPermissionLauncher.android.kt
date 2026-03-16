package com.fungames.home.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.fungames.core.ui.AppLogger
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume

@Composable
actual fun rememberLocationPermissionLauncher(
    onLocation: (lat: Double, lon: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launched when the system "Turn on location?" dialog resolves
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch { fetchLocation(context, onLocation, onDenied) }
        } else {
            onDenied()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        AppLogger.traceLog("android>>>permissionLauncher>>>$granted")
        if (granted) {
            scope.launch {
                checkSettingsThenFetch(
                    context = context,
                    onLocation = onLocation,
                    onError = onDenied,
                    onShowSystemDialog = { intentSender ->
                        locationSettingsLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    }
                )
            }
        } else {
            onDenied()
        }
    }

    return remember(permissionLauncher) {
        {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

/**
 * Uses Play Services SettingsClient to check if location is enabled.
 * If not, triggers the system "Turn on location?" dialog (no app switch needed).
 * If already enabled, proceeds straight to fetching location.
 */
private suspend fun checkSettingsThenFetch(
    context: Context,
    onLocation: (Double, Double) -> Unit,
    onError: () -> Unit,
    onShowSystemDialog: (IntentSender) -> Unit
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000).build()
    val settingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .build()

    val locationEnabled = suspendCancellableCoroutine<Boolean> { cont ->
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                AppLogger.traceLog("checkSettings>>>location already enabled")
                if (cont.isActive) cont.resume(true)
            }
            .addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    AppLogger.traceLog("checkSettings>>>showing system dialog")
                    onShowSystemDialog(e.resolution.intentSender)
                } else {
                    AppLogger.traceLog("checkSettings>>>unresolvable: ${e.message}")
                    onError()
                }
                if (cont.isActive) cont.resume(false)
            }
    }

    if (locationEnabled) {
        fetchLocation(context, onLocation, onError)
    }
}

private suspend fun fetchLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit,
    onError: () -> Unit
) {
    AppLogger.traceLog("fetchLocation")
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Suppress("DEPRECATION")
    val criteria = Criteria().apply { accuracy = Criteria.ACCURACY_COARSE }
    @Suppress("DEPRECATION")
    val provider = locationManager.getBestProvider(criteria, true) ?: run {
        AppLogger.traceLog("fetchLocation>>>no provider enabled")
        onError()
        return
    }
    AppLogger.traceLog("fetchLocation>>>provider=$provider")

    // Use cached location if it's fresh (under 2 minutes old)
    @Suppress("MissingPermission")
    val lastKnown = locationManager.getLastKnownLocation(provider)
    if (lastKnown != null && System.currentTimeMillis() - lastKnown.time < 2 * 60 * 1000L) {
        AppLogger.traceLog("fetchLocation>>>using lastKnown")
        onLocation(lastKnown.latitude, lastKnown.longitude)
        return
    }

    // Request a fresh fix with a 15s timeout
    try {
        withTimeout(15_000L) {
            suspendCancellableCoroutine { cont ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        if (cont.isActive) {
                            cont.resume(Unit)
                            AppLogger.traceLog("fetchLocation>>>onLocation fresh")
                            onLocation(location.latitude, location.longitude)
                        }
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
        }
    } catch (exception: Exception) {
        AppLogger.traceLog("fetchLocation>>>timeout/exception: ${exception.message}")
        // Timed out — use any cached location as last resort
        @Suppress("MissingPermission")
        val fallback = locationManager.getLastKnownLocation(provider)
        if (fallback != null) {
            AppLogger.traceLog("fetchLocation>>>using fallback lastKnown")
            onLocation(fallback.latitude, fallback.longitude)
        } else {
            onError()
        }
    }
}
