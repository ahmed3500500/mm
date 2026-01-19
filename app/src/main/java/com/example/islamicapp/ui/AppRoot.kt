package com.example.islamicapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Timer
import com.example.islamicapp.ui.screens.HomeScreen
import com.example.islamicapp.ui.screens.QuranScreen
import com.example.islamicapp.ui.screens.MoreScreen

enum class BottomDestination(val label: String, val icon: ImageVector) {
    Home("الرئيسية", Icons.Filled.Home),
    Quran("القرآن", Icons.Filled.QrCode),
    Tools("المزيد", Icons.Filled.MoreHoriz)
}

@Composable
fun AppRoot() {
    var current by remember { mutableStateOf(BottomDestination.Home) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomDestination.values().forEach { dest ->
                    NavigationBarItem(
                        selected = current == dest,
                        onClick = { current = dest },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (current) {
            BottomDestination.Home -> HomeScreen(Modifier.padding(padding))
            BottomDestination.Quran -> QuranScreen(Modifier.padding(padding))
            BottomDestination.Tools -> MoreScreen(Modifier.padding(padding))
        }
    }
}

