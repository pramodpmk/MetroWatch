package com.fungames.home.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSError
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionLauncher(
    onLocation: (lat: Double, lon: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    val latestOnLocation = rememberUpdatedState(onLocation)
    val latestOnDenied = rememberUpdatedState(onDenied)

    val locationManager = remember { CLLocationManager() }

    val delegate = remember {
        LocationDelegate(
            onLocationUpdate = { lat, lon -> latestOnLocation.value(lat, lon) },
            onDenied = { latestOnDenied.value() }
        )
    }

    DisposableEffect(Unit) {
        locationManager.delegate = delegate
        onDispose {
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
        }
    }

    return {
        when (locationManager.authorizationStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> locationManager.requestLocation()

            kCLAuthorizationStatusNotDetermined -> locationManager.requestWhenInUseAuthorization()

            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> latestOnDenied.value()

            else -> latestOnDenied.value()
        }
    }
}

private class LocationDelegate(
    private val onLocationUpdate: (Double, Double) -> Unit,
    private val onDenied: () -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
        val lat = location.coordinate.useContents { latitude }
        val lon = location.coordinate.useContents { longitude }
        manager.stopUpdatingLocation()
        onLocationUpdate(lat, lon)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onDenied()
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        when (manager.authorizationStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> manager.requestLocation()

            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> onDenied()

            else -> {}
        }
    }
}
