package com.example.islamicapp.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.islamicapp.R
import com.example.islamicapp.qibla.QiblaCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun QiblaScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    var azimuth by remember { mutableFloatStateOf(0f) }
    var qiblaBearing by remember { mutableFloatStateOf(0f) }
    var isAligned by remember { androidx.compose.runtime.mutableStateOf(false) }
    var distanceKm by remember { mutableFloatStateOf(0f) }
    
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    
    LaunchedEffect(Unit) {
        try {
            val result = withContext(Dispatchers.IO) {
                val bearing = QiblaCalculator.getQiblaBearing(context)
                val distance = QiblaCalculator.getDistanceToKaabaKm(context)
                Pair(bearing, distance)
            }
            val bearing = result.first
            val distance = result.second
            if (bearing != null) {
                qiblaBearing = bearing
            }
            if (distance != null) {
                distanceKm = distance
            }
        } catch (e: Exception) {
            // Handle error (e.g., location permission not granted)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        val azimuthInDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        azimuth = if (azimuthInDegrees < 0) azimuthInDegrees + 360 else azimuthInDegrees
                    } else if (it.sensor.type == Sensor.TYPE_ORIENTATION) {
                         // Fallback for older devices
                         azimuth = it.values[0]
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) 
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Calculate rotation for the compass image
    // If the phone points North (0), azimuth is 0.
    // Qibla is at qiblaBearing (e.g. 130).
    // We want the arrow to point to Qibla.
    // The arrow image should point UP by default.
    // If we rotate the image by (qiblaBearing - azimuth), it should point to Qibla relative to phone.
    val rotation = qiblaBearing - azimuth
    
    // Check alignment (within 5 degrees)
    val diff = abs(azimuth - qiblaBearing)
    val aligned = diff < 5 || diff > 355
    
    if (aligned && !isAligned) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    isAligned = aligned

    val compassColor by animateColorAsState(
        if (isAligned) Color(0xFF00FF00) else Color(0xFFFFD700),
        label = "compassColor"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "اتجاه القبلة",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color(0xFF14402A).copy(alpha = 0.8f), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().rotate(rotation),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.arrow_up_float),
                        contentDescription = "Qibla",
                        tint = compassColor,
                        modifier = Modifier.size(60.dp)
                    )
                }
                
                // Center text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${azimuth.toInt()}°",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "القبلة: ${qiblaBearing.toInt()}°",
                        color = Color(0xFFFFD700),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (isAligned) "أنت الآن باتجاه القبلة" else "قم بتدوير الهاتف حتى يصبح السهم أخضر",
                color = if (isAligned) Color(0xFF00FF00) else Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (distanceKm > 0f) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "المسافة إلى مكة تقريباً ${distanceKm.toInt()} كم",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
