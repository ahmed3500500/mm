package com.example.islamicapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.settings.AppSettings
import kotlinx.coroutines.launch
import com.example.islamicapp.adhan.NotificationManagerHelper

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.net.Uri
import android.os.PowerManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by AppSettings.observe(context).collectAsState(initial = null)
    
    // Battery Optimization Check
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val isIgnoringBatteryOptimizations = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    } else {
        true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("الإشعارات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("🔙", fontSize = 20.sp) // Simple back icon or use Icon
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF14402A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Battery Optimization Warning
            if (!isIgnoringBatteryOptimizations) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تنبيه مهم", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                        Text(
                            "قد يتم إيقاف الأذان بسبب إعدادات توفير الطاقة. يرجى استثناء التطبيق لضمان عمل الأذان.",
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Fallback to generic settings
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) {
                            Text("إصلاح المشكلة")
                        }
                    }
                }
            }

            settings?.let { pref ->
                // Section 1: Adhan
                NotificationSection(
                    title = "إشعار الأذان (وقت الصلاة)",
                    enabled = pref.notifAdhanEnabled,
                    onEnableChange = { scope.launch { AppSettings.updateNotifAdhanEnabled(context, it) } }
                ) {
                    // Adhan Mode
                    Text("نوع الصوت", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    ModeDropdown(
                        currentMode = pref.notifAdhanMode,
                        onModeSelected = { scope.launch { AppSettings.updateNotifAdhanMode(context, it) } }
                    )

                    if (pref.notifAdhanMode == "ADHAN" || pref.notifAdhanMode == "DUA" || pref.notifAdhanMode == "BEEP") {
                        Text("ملف الصوت", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        SoundDropdown(
                            currentSound = pref.notifAdhanSound,
                            mode = pref.notifAdhanMode,
                            onSoundSelected = { scope.launch { AppSettings.updateNotifAdhanSound(context, it) } }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { scope.launch { NotificationManagerHelper.testAdhanNotification(context) } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("اختبار إشعار الأذان")
                    }
                }

                // Section 2: Reminder
                NotificationSection(
                    title = "تذكير قبل الصلاة",
                    enabled = pref.reminderEnabled,
                    onEnableChange = { scope.launch { AppSettings.updateReminderEnabled(context, it) } }
                ) {
                    Text("التذكير قبل (دقيقة)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    ReminderDropdown(
                        currentMinutes = pref.reminderMinutes,
                        onMinutesSelected = { scope.launch { AppSettings.updateReminderMinutes(context, it) } }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                     Button(
                        onClick = { scope.launch { NotificationManagerHelper.testReminderNotification(context) } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("اختبار التذكير")
                    }
                }

                // Section 3: Azkar
                NotificationSection(
                    title = "إشعارات الأذكار",
                    enabled = pref.notifAzkarEnabled,
                    onEnableChange = { scope.launch { AppSettings.updateNotifAzkarEnabled(context, it) } }
                ) {
                    // Morning Azkar
                    Text("أذكار الصباح (بعد الفجر)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    AzkarDelayDropdown(
                        currentDelay = pref.azkarMorningDelay,
                        onDelaySelected = { scope.launch { AppSettings.updateAzkarMorningDelay(context, it) } }
                    )

                    // Evening Azkar
                    Text("أذكار المساء (بعد العصر)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    AzkarDelayDropdown(
                        currentDelay = pref.azkarEveningDelay,
                        onDelaySelected = { scope.launch { AppSettings.updateAzkarEveningDelay(context, it) } }
                    )

                    // Sleep Azkar
                    Text("أذكار النوم", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    SleepTimeDropdown(
                        currentTime = pref.azkarSleepTime,
                        onTimeSelected = { scope.launch { AppSettings.updateAzkarSleepTime(context, it) } }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("تشغيل صوت الدعاء")
                        Switch(
                            checked = pref.notifAzkarVoice,
                            onCheckedChange = { scope.launch { AppSettings.updateNotifAzkarVoice(context, it) } }
                        )
                    }
                     Spacer(modifier = Modifier.height(8.dp))
                     Button(
                        onClick = { scope.launch { NotificationManagerHelper.testAzkarNotification(context) } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("اختبار الأذكار")
                    }
                }

                // Section 4: Quiet Mode
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("وضع احترام الأماكن", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("يمنع تشغيل أي صوت ديني (نص فقط)", fontSize = 12.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = pref.quietMode,
                                onCheckedChange = { scope.launch { AppSettings.updateQuietMode(context, it) } }
                            )
                        }
                    }
                }

                // Section 5: High Privacy Mode
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("وضع الخصوصية العالية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(
                                    "يخفي محتوى الإشعار الديني ويعرض تنبيهًا عامًا فقط",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = pref.highPrivacyMode,
                                onCheckedChange = { value ->
                                    scope.launch { AppSettings.updateHighPrivacyMode(context, value) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarDelayDropdown(currentDelay: Int, onDelaySelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(0, 15, 30, 45, 60)

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (currentDelay == 0) "مباشرة" else "بعد $currentDelay دقيقة",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { min ->
                    DropdownMenuItem(
                        text = { Text(if (min == 0) "مباشرة" else "بعد $min دقيقة") },
                        onClick = {
                            onDelaySelected(min)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimeDropdown(currentTime: String, onTimeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("20:00", "21:00", "22:00", "23:00", "00:00")

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currentTime,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { time ->
                    DropdownMenuItem(
                        text = { Text(time) },
                        onClick = {
                            onTimeSelected(time)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationSection(
    title: String,
    enabled: Boolean,
    onEnableChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF14402A))
                Switch(checked = enabled, onCheckedChange = onEnableChange)
            }
            if (enabled) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeDropdown(currentMode: String, onModeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val modes = mapOf(
        "ADHAN" to "أذان كامل",
        "DUA" to "دعاء (بديل)",
        "BEEP" to "تنبيه بسيط",
        "SILENT" to "صامت"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = modes[currentMode] ?: currentMode,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                modes.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onModeSelected(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundDropdown(currentSound: String, mode: String, onSoundSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    // Dynamic list based on mode
    val sounds = when (mode) {
        "ADHAN" -> listOf("adhan_1", "adhan_2", "adhan_3")
        "DUA" -> listOf("dua_1", "dua_2")
        "BEEP" -> listOf("beep_1", "beep_2")
        else -> emptyList()
    }

    if (sounds.isEmpty()) return

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currentSound,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sounds.forEach { sound ->
                    DropdownMenuItem(
                        text = { Text(sound) },
                        onClick = {
                            onSoundSelected(sound)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDropdown(currentMinutes: Int, onMinutesSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(5, 10, 15, 30)

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "$currentMinutes دقائق",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { min ->
                    DropdownMenuItem(
                        text = { Text("$min دقائق") },
                        onClick = {
                            onMinutesSelected(min)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
