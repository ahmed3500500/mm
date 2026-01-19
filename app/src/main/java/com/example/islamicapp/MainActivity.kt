package com.example.islamicapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.islamicapp.ui.AppRoot
import com.example.islamicapp.ui.theme.IslamicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IslamicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PermissionHandler()
                        AppRoot()
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionHandler() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= 33) {
        var hasPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        
        var showExplanation by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasPermission = isGranted
            }
        )

        LaunchedEffect(Unit) {
            if (!hasPermission) {
                showExplanation = true
            }
        }

        if (showExplanation && !hasPermission) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismiss logic if needed, but allow for now */ },
                title = { Text("تفعيل الأذان والإشعارات") },
                text = { Text("لضمان وصول الأذان والتذكيرات في وقتها، يرجى السماح للتطبيق بإرسال الإشعارات.") },
                confirmButton = {
                    Button(onClick = {
                        showExplanation = false
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }) {
                        Text("موافق")
                    }
                },
                dismissButton = {
                    Button(onClick = { showExplanation = false }) {
                        Text("لاحقاً")
                    }
                }
            )
        }
    }
}

