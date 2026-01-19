package com.example.islamicapp.qibla

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object QiblaCalculator {
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LON = 39.8262

    suspend fun getQiblaBearing(context: Context): Float? {
        val location = getLastLocation(context) ?: return null
        return bearingToKaaba(location.latitude, location.longitude)
    }

    suspend fun getDistanceToKaabaKm(context: Context): Float? {
        val location = getLastLocation(context) ?: return null
        return distanceKm(location.latitude, location.longitude, KAABA_LAT, KAABA_LON)
    }

    private suspend fun getLastLocation(context: Context): Location? {
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    private fun bearingToKaaba(lat: Double, lon: Double): Float {
        val phi1 = Math.toRadians(lat)
        val phi2 = Math.toRadians(KAABA_LAT)
        val lambda1 = Math.toRadians(lon)
        val lambda2 = Math.toRadians(KAABA_LON)
        val numerator = sin(lambda2 - lambda1)
        val denominator = cos(phi1) * tan(phi2) - sin(phi1) * cos(lambda2 - lambda1)
        val theta = atan2(numerator, denominator)
        var bearing = Math.toDegrees(theta)
        if (bearing < 0) bearing += 360.0
        return bearing.toFloat()
    }

    private fun distanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val r = 6371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val meters = r * c
        return (meters / 1000.0).toFloat()
    }
}
