package com.metrowatch.kochi.home.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.metrowatch.kochi.ui.AppLogger
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.CancellationTokenSource
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
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    val cts = CancellationTokenSource()

    val location = suspendCancellableCoroutine<android.location.Location?> { cont ->
        @Suppress("MissingPermission")
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                AppLogger.traceLog("fetchLocation>>>getCurrentLocation success loc=$loc")
                if (cont.isActive) cont.resume(loc)
            }
            .addOnFailureListener { e ->
                AppLogger.traceLog("fetchLocation>>>getCurrentLocation failure: ${e.message}")
                if (cont.isActive) cont.resume(null)
            }
        cont.invokeOnCancellation { cts.cancel() }
    }

    if (location != null) {
        onLocation(location.latitude, location.longitude)
        return
    }

    // getCurrentLocation returned null — fall back to last known
    @Suppress("MissingPermission")
    fusedClient.lastLocation
        .addOnSuccessListener { last ->
            if (last != null) {
                AppLogger.traceLog("fetchLocation>>>using lastKnown")
                onLocation(last.latitude, last.longitude)
            } else {
                AppLogger.traceLog("fetchLocation>>>no location available")
                onError()
            }
        }
        .addOnFailureListener {
            AppLogger.traceLog("fetchLocation>>>lastLocation failure")
            onError()
        }
}
