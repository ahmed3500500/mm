package com.example.islamicapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.R
import com.example.islamicapp.quran.Reciter
import com.example.islamicapp.settings.AppSettings
import kotlinx.coroutines.launch

enum class AppLanguage(val code: String, val displayName: String) {
    Arabic("ar", "العربية"),
    English("en", "English"),
    French("fr", "Français"),
    Spanish("es", "Español")
}

data class SettingsStrings(
    val screenTitle: String,
    val locationSectionTitle: String,
    val autoGpsTitle: String,
    val manualCityTitle: String,
    val manualCityUnset: String,
    val quranAudioSectionTitle: String,
    val adhanSectionTitle: String,
    val adhanEnabledTitle: String,
    val notificationsEnabledTitle: String,
    val backgroundWorkTitle: String,
    val appearanceSectionTitle: String,
    val darkModeTitle: String,
    val fontSizeTitle: String,
    val fontSizeCurrentPrefix: String,
    val languageSectionTitle: String,
    val languageTitle: String,
    val reciterTitle: String,
    val reciterDescription: String
)

private fun appLanguageFromCode(code: String): AppLanguage {
    return AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.Arabic
}

private fun settingsStringsFor(language: AppLanguage): SettingsStrings {
    return when (language) {
        AppLanguage.Arabic -> SettingsStrings(
            screenTitle = "الإعدادات",
            locationSectionTitle = "الموقع والمدينة",
            autoGpsTitle = "تحديد الموقع تلقائياً (GPS)",
            manualCityTitle = "المدينة الحالية",
            manualCityUnset = "جاري التحديد...",
            quranAudioSectionTitle = "القرآن الصوتي",
            adhanSectionTitle = "الأذان والإشعارات",
            adhanEnabledTitle = "تفعيل الأذان",
            notificationsEnabledTitle = "تفعيل الإشعارات",
            backgroundWorkTitle = "العمل في الخلفية 24/7",
            appearanceSectionTitle = "المظهر والخط",
            darkModeTitle = "الوضع الليلي",
            fontSizeTitle = "حجم الخط",
            fontSizeCurrentPrefix = "الحجم الحالي:",
            languageSectionTitle = "اللغة",
            languageTitle = "اختر لغة التطبيق",
            reciterTitle = "القارئ",
            reciterDescription = "اختر القارئ الافتراضي للقرآن الصوتي"
        )
        AppLanguage.English -> SettingsStrings(
            screenTitle = "Settings",
            locationSectionTitle = "Location and City",
            autoGpsTitle = "Detect location automatically (GPS)",
            manualCityTitle = "Manual city",
            manualCityUnset = "Not set",
            quranAudioSectionTitle = "Quran Audio",
            adhanSectionTitle = "Adhan and Notifications",
            adhanEnabledTitle = "Enable Adhan",
            notificationsEnabledTitle = "Enable notifications",
            backgroundWorkTitle = "Background work 24/7",
            appearanceSectionTitle = "Appearance and font",
            darkModeTitle = "Dark mode",
            fontSizeTitle = "Font size",
            fontSizeCurrentPrefix = "Current size:",
            languageSectionTitle = "Language",
            languageTitle = "Choose app language",
            reciterTitle = "Reciter",
            reciterDescription = "Choose default reciter for Quran audio"
        )
        AppLanguage.French -> SettingsStrings(
            screenTitle = "Paramètres",
            locationSectionTitle = "Localisation et ville",
            autoGpsTitle = "Détecter la position automatiquement (GPS)",
            manualCityTitle = "Ville manuelle",
            manualCityUnset = "Non définie",
            quranAudioSectionTitle = "Coran audio",
            adhanSectionTitle = "Adhan et notifications",
            adhanEnabledTitle = "Activer l'adhan",
            notificationsEnabledTitle = "Activer les notifications",
            backgroundWorkTitle = "Fonctionnement en arrière-plan 24h/24",
            appearanceSectionTitle = "Apparence et police",
            darkModeTitle = "Mode sombre",
            fontSizeTitle = "Taille de la police",
            fontSizeCurrentPrefix = "Taille actuelle :",
            languageSectionTitle = "Langue",
            languageTitle = "Choisissez la langue de l'application",
            reciterTitle = "Récitateur",
            reciterDescription = "Choisissez le récitateur par défaut du Coran audio"
        )
        AppLanguage.Spanish -> SettingsStrings(
            screenTitle = "Configuración",
            locationSectionTitle = "Ubicación y ciudad",
            autoGpsTitle = "Detectar ubicación automáticamente (GPS)",
            manualCityTitle = "Ciudad manual",
            manualCityUnset = "No definida",
            quranAudioSectionTitle = "Corán audio",
            adhanSectionTitle = "Adhan y notificaciones",
            adhanEnabledTitle = "Activar adhan",
            notificationsEnabledTitle = "Activar notificaciones",
            backgroundWorkTitle = "Trabajo en segundo plano 24/7",
            appearanceSectionTitle = "Apariencia y fuente",
            darkModeTitle = "Modo oscuro",
            fontSizeTitle = "Tamaño de fuente",
            fontSizeCurrentPrefix = "Tamaño actual:",
            languageSectionTitle = "Idioma",
            languageTitle = "Elija el idioma de la aplicación",
            reciterTitle = "Recitador",
            reciterDescription = "Elija el recitador predeterminado del Corán audio"
        )
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onOpenNotifications: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by AppSettings.observe(context).collectAsState(
        initial = com.example.islamicapp.settings.SettingsState()
    )

    val appLanguage = appLanguageFromCode(settings.languageCode)
    val strings = settingsStringsFor(appLanguage)

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SettingsSectionTitle(strings.locationSectionTitle)
                }
                item {
                    SettingsTextRow(
                        title = strings.manualCityTitle,
                        value = if (settings.city.isEmpty()) strings.manualCityUnset else settings.city
                    )
                }

                item {
                    SettingsSectionTitle(strings.quranAudioSectionTitle)
                }
                item {
                    ReciterSettingsRow(
                        title = strings.reciterTitle,
                        description = strings.reciterDescription,
                        selected = settings.reciter,
                        onSelect = { reciter ->
                            scope.launch { AppSettings.updateReciter(context, reciter) }
                        }
                    )
                }

                item {
                    SettingsSectionTitle(strings.adhanSectionTitle)
                }
                item {
                    // Button to open Notifications Screen
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenNotifications),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تخصيص الإشعارات والأصوات",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "➜",
                                color = Color(0xFFFFD700),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                item {
                    SettingsSwitchRow(
                        title = strings.adhanEnabledTitle,
                        checked = settings.adhanEnabled,
                        onCheckedChange = { value ->
                            scope.launch { AppSettings.updateAdhanEnabled(context, value) }
                        }
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = strings.notificationsEnabledTitle,
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { value ->
                            scope.launch { AppSettings.updateNotificationsEnabled(context, value) }
                        }
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = strings.backgroundWorkTitle,
                        checked = settings.backgroundWorkEnabled,
                        onCheckedChange = { value ->
                            scope.launch { AppSettings.updateBackgroundEnabled(context, value) }
                        }
                    )
                }

                item {
                    SettingsSectionTitle(strings.appearanceSectionTitle)
                }
                item {
                    SettingsSwitchRow(
                        title = strings.darkModeTitle,
                        checked = settings.darkModeEnabled,
                        onCheckedChange = { value ->
                            scope.launch { AppSettings.updateDarkMode(context, value) }
                        }
                    )
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = strings.fontSizeTitle,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = settings.fontScale,
                                onValueChange = { value ->
                                    scope.launch { AppSettings.updateFontScale(context, value) }
                                },
                                valueRange = 0.8f..1.4f
                            )
                            Text(
                                text = "${strings.fontSizeCurrentPrefix} ${(settings.fontScale * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                item {
                    SettingsSectionTitle(strings.languageSectionTitle)
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = strings.languageTitle,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LanguageChipRow(
                                selected = appLanguage,
                                onSelect = { language ->
                                    scope.launch {
                                        AppSettings.updateLanguage(context, language.code)
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun SettingsTextRow(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ReciterSettingsRow(
    title: String,
    description: String,
    selected: Reciter,
    onSelect: (Reciter) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Color.White,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            ReciterChipRow(selected = selected, onSelect = onSelect)
        }
    }
}

@Composable
fun ReciterChipRow(
    selected: Reciter,
    onSelect: (Reciter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReciterChipItem(reciter = Reciter.AL_AFASY, selected = selected, onSelect = onSelect)
        ReciterChipItem(reciter = Reciter.SAAD_GHAMDI, selected = selected, onSelect = onSelect)
        ReciterChipItem(reciter = Reciter.MAHER, selected = selected, onSelect = onSelect)
    }
}

@Composable
fun ReciterChipItem(
    reciter: Reciter,
    selected: Reciter,
    onSelect: (Reciter) -> Unit
) {
    val background = if (selected == reciter) Color(0xFFFFD700) else Color(0xFF0F3B24)
    val textColor = if (selected == reciter) Color(0xFF062D1A) else Color.White
    Card(
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reciter.displayName,
                color = textColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun LanguageChipRow(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LanguageChipItem(language = AppLanguage.Arabic, selected = selected, onSelect = onSelect)
        LanguageChipItem(language = AppLanguage.English, selected = selected, onSelect = onSelect)
        LanguageChipItem(language = AppLanguage.French, selected = selected, onSelect = onSelect)
        LanguageChipItem(language = AppLanguage.Spanish, selected = selected, onSelect = onSelect)
    }
}

@Composable
fun LanguageChipItem(
    language: AppLanguage,
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit
) {
    val background = if (selected == language) Color(0xFFFFD700) else Color(0xFF0F3B24)
    val textColor = if (selected == language) Color(0xFF062D1A) else Color.White
    Card(
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { onSelect(language) }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.displayName,
                color = textColor,
                fontSize = 12.sp
            )
        }
    }
}
