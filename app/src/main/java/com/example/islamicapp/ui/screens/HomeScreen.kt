package com.example.islamicapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.islamicapp.R
import com.example.islamicapp.adhan.AdhanScheduler
import com.example.islamicapp.prayer.PrayerTimesUiState
import com.example.islamicapp.settings.AppSettings
import com.example.islamicapp.prayer.PrayerTimesViewModel
import com.example.islamicapp.settings.DailyIbadahState
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.example.islamicapp.settings.SettingsState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayerTimesViewModel = viewModel(),
    onOpenQuranAudio: () -> Unit = {},
    onOpenQuranText: () -> Unit = {},
    onOpenTasbeeh: () -> Unit = {},
    onOpenDhikr: () -> Unit = {},
    onOpenQibla: () -> Unit = {},
    onOpenNames: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val settingsFlow = remember { AppSettings.observe(context) }
    val settings by settingsFlow.collectAsState(
        initial = com.example.islamicapp.settings.SettingsState()
    )
    val ibadahFlow = remember { AppSettings.observeDailyIbadah(context) }
    val ibadahState by ibadahFlow.collectAsState(
        initial = DailyIbadahState()
    )
    var now by remember { mutableStateOf(LocalDateTime.now(ZoneId.systemDefault())) }
    val scope = rememberCoroutineScope()
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            scope.launch {
                loadLocationAndTimings(context, viewModel)
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now(ZoneId.systemDefault())
            kotlinx.coroutines.delay(1000L)
        }
    }
    LaunchedEffect(settings.useGps) {
        if (settings.useGps) {
            val fine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val coarse = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (fine || coarse) {
                loadLocationAndTimings(context, viewModel)
            } else {
                locationLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else if (settings.city.isNotEmpty()) {
            viewModel.refreshTimingsForCity(settings.city)
        }
    }

    LaunchedEffect(state, settings) {
        if (settings.backgroundWorkEnabled && !state.isLoading && state.error == null) {
            AdhanScheduler.schedulePrayers(context, state, settings)
        }
    }
    
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val timeText = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a", Locale("ar")))
            val dateGregorian = now.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الوقت الآن: $timeText",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "التاريخ الميلادي: $dateGregorian",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
                if (state.hijriDate.isNotEmpty()) {
                    Text(
                        text = "التاريخ الهجري: ${state.hijriDate}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
            }

            Text(
                text = "مواقيت الصلاة لليوم",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
            
            PrayerSection(state = state, onRetry = { viewModel.refreshTimings() })
            
            Spacer(modifier = Modifier.height(8.dp))

            DailyIntentionSection(
                current = settings.dailyIntention,
                onChange = { value ->
                    scope.launch { AppSettings.updateDailyIntention(context, value) }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            val dayOfYear = now.dayOfYear
            val dailyHadith = dailyAhadith[dayOfYear % dailyAhadith.size]

            DailyHadithSection(hadith = dailyHadith)

            Spacer(modifier = Modifier.height(8.dp))

            DailyIbadahSection(
                state = ibadahState,
                onToggleFajr = { value ->
                    scope.launch { AppSettings.setFajrDone(context, value) }
                },
                onToggleDhuhr = { value ->
                    scope.launch { AppSettings.setDhuhrDone(context, value) }
                },
                onToggleAsr = { value ->
                    scope.launch { AppSettings.setAsrDone(context, value) }
                },
                onToggleMaghrib = { value ->
                    scope.launch { AppSettings.setMaghribDone(context, value) }
                },
                onToggleIsha = { value ->
                    scope.launch { AppSettings.setIshaDone(context, value) }
                },
                onToggleQuran = { value ->
                    scope.launch { AppSettings.setQuranDone(context, value) }
                },
                onToggleDhikr = { value ->
                    scope.launch { AppSettings.setDhikrDone(context, value) }
                },
                onToggleNawafil = { value ->
                    scope.launch { AppSettings.setNawafilDone(context, value) }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            DailySunanSection()

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FeatureCard(
                        title = "القرآن الكريم صوت",
                        subtitle = "تلاوة مشاري العفاسي",
                        onClick = onOpenQuranAudio
                    )
                }
                item {
                    FeatureCard(
                        title = "القرآن الكريم قراءة",
                        subtitle = "النص القرآني الكامل بدون تفسير",
                        onClick = onOpenQuranText
                    )
                }
                item {
                    FeatureCard(
                        title = "السبحة الإلكترونية",
                        subtitle = "عد تسبيح متقدم",
                        onClick = onOpenTasbeeh
                    )
                }
                item {
                    FeatureCard(
                        title = "أذكارك اليومية",
                        subtitle = "الصباح، المساء، النوم",
                        onClick = onOpenDhikr
                    )
                }
                item {
                    FeatureCard(
                        title = "اتجاه القبلة",
                        subtitle = "تحديد القبلة بدقة",
                        onClick = onOpenQibla
                    )
                }
                item {
                    FeatureCard(
                        title = "أسماء الله الحسنى",
                        subtitle = "شرح 99 اسم",
                        onClick = onOpenNames
                    )
                }
                item {
                    FeatureCard(
                        title = "الإعدادات",
                        subtitle = "المدينة، الصوت، الثيم",
                        onClick = onOpenSettings
                    )
                }
            }
        }
    }
}

private fun formatTo12Hour(time: String): String {
    return try {
        val clean = time.substringBefore(" ")
        val parts = clean.split(":")
        if (parts.size < 2) return time
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val localTime = LocalTime.of(hour, minute)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("ar"))
        localTime.format(formatter)
    } catch (e: Exception) {
        time
    }
}

@Composable
fun PrayerSection(state: PrayerTimesUiState, onRetry: () -> Unit) {
    if (state.isLoading) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F3B24).copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFFFFD700))
                Text(text = "جاري تحميل مواقيت الصلاة", color = Color.White)
            }
        }
        return
    }
    if (state.error != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F3B24).copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = state.error, color = Color.White)
                Text(text = "تحقق من الاتصال بالإنترنت ثم أعد المحاولة", color = Color.White, fontSize = 12.sp)
            }
        }
        return
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F3B24).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "المدينة: ${state.cityArabic}",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "التاريخ الهجري: ${state.hijriDate}",
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = "الصلاة القادمة: ${state.nextPrayerName} بعد ${state.nextPrayerRemaining}",
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val highlightColor = Color(0xFFFFD700)
                val normalColor = Color.White
                val nextName = state.nextPrayerName

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "الفجر", color = Color(0xFFFFD700))
                    Text(
                        text = formatTo12Hour(state.fajr),
                        color = if (nextName == "الفجر") highlightColor else normalColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "الشروق", color = Color(0xFFFFD700))
                    Text(text = formatTo12Hour(state.sunrise), color = normalColor)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "الظهر", color = Color(0xFFFFD700))
                    Text(
                        text = formatTo12Hour(state.dhuhr),
                        color = if (nextName == "الظهر") highlightColor else normalColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "العصر", color = Color(0xFFFFD700))
                    Text(
                        text = formatTo12Hour(state.asr),
                        color = if (nextName == "العصر") highlightColor else normalColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "المغرب", color = Color(0xFFFFD700))
                    Text(
                        text = formatTo12Hour(state.maghrib),
                        color = if (nextName == "المغرب") highlightColor else normalColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "العشاء", color = Color(0xFFFFD700))
                    Text(
                        text = formatTo12Hour(state.isha),
                        color = if (nextName == "العشاء") highlightColor else normalColor
                    )
                }
            }
        }
    }
}

@Composable
fun DailyIbadahSection(
    state: DailyIbadahState,
    onToggleFajr: (Boolean) -> Unit,
    onToggleDhuhr: (Boolean) -> Unit,
    onToggleAsr: (Boolean) -> Unit,
    onToggleMaghrib: (Boolean) -> Unit,
    onToggleIsha: (Boolean) -> Unit,
    onToggleQuran: (Boolean) -> Unit,
    onToggleDhikr: (Boolean) -> Unit,
    onToggleNawafil: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "عبادات اليوم",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DailyIbadahChip(
                    title = "الفجر",
                    done = state.fajrDone,
                    onToggle = onToggleFajr
                )
                DailyIbadahChip(
                    title = "الظهر",
                    done = state.dhuhrDone,
                    onToggle = onToggleDhuhr
                )
                DailyIbadahChip(
                    title = "العصر",
                    done = state.asrDone,
                    onToggle = onToggleAsr
                )
                DailyIbadahChip(
                    title = "المغرب",
                    done = state.maghribDone,
                    onToggle = onToggleMaghrib
                )
                DailyIbadahChip(
                    title = "العشاء",
                    done = state.ishaDone,
                    onToggle = onToggleIsha
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DailyIbadahChip(
                    title = "قرآن",
                    done = state.quranDone,
                    onToggle = onToggleQuran
                )
                DailyIbadahChip(
                    title = "ذكر",
                    done = state.dhikrDone,
                    onToggle = onToggleDhikr
                )
                DailyIbadahChip(
                    title = "نوافل",
                    done = state.nawafilDone,
                    onToggle = onToggleNawafil
                )
            }
        }
    }
}

@Composable
fun DailyIntentionSection(
    current: String,
    onChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "نية اليوم",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "اختر نيتك لليوم بلطف: عمل، صبر، أو عبادة.",
                color = Color.White,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IntentionChip(
                    title = "عمل",
                    selected = current == "عمل",
                    onClick = { onChange("عمل") }
                )
                IntentionChip(
                    title = "صبر",
                    selected = current == "صبر",
                    onClick = { onChange("صبر") }
                )
                IntentionChip(
                    title = "عبادة",
                    selected = current == "عبادة",
                    onClick = { onChange("عبادة") }
                )
            }
        }
    }
}

data class DailyHadith(
    val text: String,
    val explanation: String
)

private val dailyAhadith = listOf(
    DailyHadith(
        text = "قال رسول الله ﷺ: \"إنما الأعمال بالنيات\".",
        explanation = "اجعل كل عمل اليوم خالصًا لله."
    ),
    DailyHadith(
        text = "قال رسول الله ﷺ: \"لا تحقرن من المعروف شيئًا\".",
        explanation = "قدّم معروفًا بسيطًا ولو بكلمة طيبة."
    ),
    DailyHadith(
        text = "قال رسول الله ﷺ: \"الدين النصيحة\".",
        explanation = "اختر نصيحة لطيفة لقريب أو صديق اليوم."
    ),
    DailyHadith(
        text = "قال رسول الله ﷺ: \"اتق الله حيثما كنت\".",
        explanation = "راقب الله في كلامك وعملك طوال اليوم."
    )
)

@Composable
fun DailyHadithSection(
    hadith: DailyHadith
) {
    var applied by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "الحديث المختار لليوم",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = hadith.text,
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = hadith.explanation,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
            Button(
                onClick = { applied = !applied },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (applied) Color(0xFF0B5B34) else Color(0xFFFFD700),
                    contentColor = if (applied) Color.White else Color(0xFF062D1A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (applied) "تم تطبيق الحديث اليوم" else "تطبيق الحديث اليوم",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DailySunanSection() {
    var wuduDone by remember { mutableStateOf(false) }
    var salahDone by remember { mutableStateOf(false) }
    var sleepDone by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "السنن اليومية",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "سنن الوضوء، سنن الصلاة، سنن النوم (اختياري).",
                color = Color.White,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DailyIbadahChip(
                    title = "سنن الوضوء",
                    done = wuduDone,
                    onToggle = { wuduDone = it }
                )
                DailyIbadahChip(
                    title = "سنن الصلاة",
                    done = salahDone,
                    onToggle = { salahDone = it }
                )
                DailyIbadahChip(
                    title = "سنن النوم",
                    done = sleepDone,
                    onToggle = { sleepDone = it }
                )
            }
        }
    }
}

@Composable
fun IntentionChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Color(0xFFFFD700) else Color(0xFF0F3B24)
    val contentColor = if (selected) Color(0xFF062D1A) else Color.White
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(50.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DailyIbadahChip(
    title: String,
    done: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val background = if (done) Color(0xFFFFD700) else Color(0xFF0F3B24)
    val contentColor = if (done) Color(0xFF062D1A) else Color.White
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(50.dp),
        onClick = { onToggle(!done) }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF14402A).copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.White, fontSize = 12.sp)
        }
    }
}

private suspend fun loadLocationAndTimings(
    context: android.content.Context,
    viewModel: PrayerTimesViewModel
) {
    var cityName: String? = null
    try {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val location = client.lastLocation.await()
        if (location != null) {
            try {
                val geocoder = Geocoder(context, Locale("ar"))
                @Suppress("DEPRECATION")
                val addresses = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                }
                if (!addresses.isNullOrEmpty()) {
                    cityName = addresses[0].locality
                        ?: addresses[0].subAdminArea
                        ?: addresses[0].adminArea
                }
            } catch (e: Exception) {
            }
            if (!cityName.isNullOrEmpty()) {
                AppSettings.updateCity(context, cityName!!)
            }
            viewModel.refreshTimingsForLocation(location.latitude, location.longitude, cityName)
        }
    } catch (e: Exception) {
    }
    }
}
