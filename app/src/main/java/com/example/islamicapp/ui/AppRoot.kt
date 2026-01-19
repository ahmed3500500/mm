package com.example.islamicapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
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
import com.example.islamicapp.ui.screens.DhikrScreen
import com.example.islamicapp.ui.screens.HomeScreen
import com.example.islamicapp.ui.screens.MoreScreen
import com.example.islamicapp.ui.screens.NamesScreen
import com.example.islamicapp.ui.screens.NotificationsScreen
import com.example.islamicapp.ui.screens.QiblaScreen
import com.example.islamicapp.ui.screens.QuranScreen
import com.example.islamicapp.ui.screens.QuranTextScreen
import com.example.islamicapp.ui.screens.SettingsScreen
import com.example.islamicapp.ui.screens.TasbeehScreen

enum class AppDestination(val label: String, val icon: ImageVector, val isBottomItem: Boolean) {
    Home("الرئيسية", Icons.Filled.Home, true),
    QuranAudio("القرآن صوت", Icons.Filled.QrCode, true),
    QuranText("القرآن قراءة", Icons.Filled.MenuBook, false),
    More("المزيد", Icons.Filled.MoreHoriz, true),
    Tasbeeh("السبحة", Icons.Filled.Timer, false),
    Dhikr("الأذكار", Icons.Filled.MenuBook, false),
    Qibla("القبلة", Icons.Filled.Explore, false),
    Names("الأسماء", Icons.Filled.List, false),
    Settings("الإعدادات", Icons.Filled.Settings, false),
    Notifications("الإشعارات", Icons.Filled.Settings, false)
}

@Composable
fun AppRoot() {
    var backStack by remember { mutableStateOf(listOf(AppDestination.Home)) }
    val current = backStack.last()

    BackHandler(enabled = backStack.size > 1) {
        backStack = backStack.dropLast(1)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestination.values().filter { it.isBottomItem }.forEach { dest ->
                    NavigationBarItem(
                        selected = current == dest,
                        onClick = {
                            if (dest == AppDestination.Home) {
                                backStack = listOf(AppDestination.Home)
                            } else if (current != dest) {
                                backStack = backStack + dest
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (current) {
            AppDestination.Home -> HomeScreen(
                modifier = Modifier.padding(padding),
                onOpenQuranAudio = { backStack = backStack + AppDestination.QuranAudio },
                onOpenQuranText = { backStack = backStack + AppDestination.QuranText },
                onOpenTasbeeh = { backStack = backStack + AppDestination.Tasbeeh },
                onOpenDhikr = { backStack = backStack + AppDestination.Dhikr },
                onOpenQibla = { backStack = backStack + AppDestination.Qibla },
                onOpenNames = { backStack = backStack + AppDestination.Names },
                onOpenSettings = { backStack = backStack + AppDestination.Settings }
            )
            AppDestination.QuranAudio -> QuranScreen(Modifier.padding(padding))
            AppDestination.QuranText -> QuranTextScreen(Modifier.padding(padding))
            AppDestination.More -> MoreScreen(Modifier.padding(padding))
            AppDestination.Tasbeeh -> TasbeehScreen(Modifier.padding(padding))
            AppDestination.Dhikr -> DhikrScreen(Modifier.padding(padding))
            AppDestination.Qibla -> QiblaScreen(Modifier.padding(padding))
            AppDestination.Names -> NamesScreen(Modifier.padding(padding))
            AppDestination.Settings -> SettingsScreen(
                modifier = Modifier.padding(padding),
                onOpenNotifications = { backStack = backStack + AppDestination.Notifications }
            )
            AppDestination.Notifications -> NotificationsScreen(Modifier.padding(padding))
        }
    }
}        }
    }
}
