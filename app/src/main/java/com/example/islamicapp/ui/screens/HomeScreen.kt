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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Surface
import androidx.compose.material3.IconButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.filled.Check
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

data class GuideItem(val title: String, val subtitle: String, val actions: List<String>)

data class Story(val title: String, val content: String)

@Composable
fun GuideCard(item: GuideItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A).copy(alpha = 0.9f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(4.dp)) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

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
    var showNightPrayer by remember { mutableStateOf(false) }
    var showFamilySection by remember { mutableStateOf(false) }
    var showRighteousPath by remember { mutableStateOf(false) }
    var showMentalPeace by remember { mutableStateOf(false) }
    var showSeasonalWorship by remember { mutableStateOf(false) }
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

            MuslimGuideSection()

            Spacer(modifier = Modifier.height(8.dp))
            
            DailyStorySection()

            Spacer(modifier = Modifier.height(8.dp))

            GoodDeedsSection()

            Spacer(modifier = Modifier.height(8.dp))

            DailyActionSuggestionSection()

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
                        title = "قيام الليل",
                        subtitle = "برنامج صلاة الليل",
                        onClick = { showNightPrayer = true }
                    )
                }
                item {
                    FeatureCard(
                        title = "واحة الأسرة",
                        subtitle = "أذكار وأدعية للأسرة",
                        onClick = { showFamilySection = true }
                    )
                }
                item {
                    FeatureCard(
                        title = "طريق الاستقامة",
                        subtitle = "خطة للصلاة أو حفظ القرآن",
                        onClick = { showRighteousPath = true }
                    )
                }
                item {
                    FeatureCard(
                        title = "السكينة النفسية",
                        subtitle = "أذكار لتهدئة القلب بدون صوت",
                        onClick = { showMentalPeace = true }
                    )
                }
                item {
                    FeatureCard(
                        title = "العبادات الموسمية",
                        subtitle = "خطط رمضان، العشر الأواخر، الحج",
                        onClick = { showSeasonalWorship = true }
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
        
        if (showNightPrayer) {
            NightPrayerDialog(onDismiss = { showNightPrayer = false })
        }
        if (showFamilySection) {
            FamilySectionDialog(onDismiss = { showFamilySection = false })
        }
        if (showRighteousPath) {
            RighteousPathDialog(onDismiss = { showRighteousPath = false })
        }
        if (showMentalPeace) {
            MentalPeaceDialog(onDismiss = { showMentalPeace = false })
        }
        if (showSeasonalWorship) {
            SeasonalWorshipDialog(onDismiss = { showSeasonalWorship = false })
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

@Composable
fun MuslimGuideSection() {
    val items = listOf(
        GuideItem("قبل النوم", "آداب وأذكار النوم", listOf("الوضوء", "نفض الفراش", "قراءة المعوذات", "آية الكرسي", "سورة الملك")),
        GuideItem("بعد الاستيقاظ", "سنن الاستيقاظ", listOf("مسح الوجه", "دعاء الاستيقاظ", "السواك", "غسل اليدين")),
        GuideItem("عند الضيق", "علاج الهم والحزن", listOf("الوضوء والصلاة", "دعاء الكرب", "الاستغفار", "الصدقة")),
        GuideItem("عند الفرح", "شكر النعمة", listOf("سجود الشكر", "الحمد والثناء", "الصدقة", "التواضع"))
    )
    var selectedItem by remember { mutableStateOf<GuideItem?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "دليل المسلم اليومي",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item: GuideItem ->
                GuideCard(
                    item = item,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedItem = item }
                )
            }
        }
    }

    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            title = { Text(text = selectedItem!!.title, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = selectedItem!!.subtitle, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                    selectedItem!!.actions.forEach { action ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0B5B34), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = action)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

data class GuideItem(val title: String, val subtitle: String, val actions: List<String>)

@Composable
fun GuideCard(item: GuideItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A).copy(alpha = 0.9f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(4.dp)) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun DailyStorySection() {
    val stories = listOf(
        Story("قصة الصحابي أبو بكر الصديق", "أول الخلفاء الراشدين، وأحب الرجال إلى النبي صلى الله عليه وسلم. كان رفيقه في الهجرة، وأنفق ماله كله في سبيل الله. موقفه يوم وفاة النبي ثبت الأمة."),
        Story("قصة التابعي سعيد بن المسيب", "سيد التابعين، رفض تزويج ابنته للخليفة وزوجها لطالب علم فقير بدرهمين. كان لا تفوته تكبيرة الإحرام في المسجد أربعين سنة."),
        Story("قصة عمر بن الخطاب", "الفاروق الذي فرق الله به بين الحق والباطل. إسلامه كان عزة للمسلمين. عدله ملأ الأرض، وكان يخشى الله في الدابة لو عثرت في العراق."),
        Story("قصة عثمان بن عفان", "ذو النورين، تستحي منه الملائكة. جهز جيش العسرة، واشترى بئر رومة للمسلمين. جمع القرآن الكريم في مصحف واحد.")
    )
    val dayOfYear = java.time.LocalDate.now().dayOfYear
    val story = stories[dayOfYear % stories.size]
    var showStory by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { showStory = true },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3436)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "قصة اليوم",
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = story.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

    if (showStory) {
        AlertDialog(
            onDismissRequest = { showStory = false },
            title = { Text(text = story.title, fontWeight = FontWeight.Bold) },
            text = {
                Text(text = story.content, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = { showStory = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun GoodDeedsSection() {
    val deeds = listOf("صدقة ولو قليلة", "بر الوالدين", "إماطة الأذى", "التبسم في وجه أخيك", "ذكر الله", "صلة الرحم")
    var completedDeeds by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ميزان الأعمال (تذكير يومي)",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(deeds) { deed ->
                FilterChip(
                    selected = completedDeeds.contains(deed),
                    onClick = {
                        completedDeeds = if (completedDeeds.contains(deed)) {
                            completedDeeds - deed
                        } else {
                            completedDeeds + deed
                        }
                    },
                    label = { Text(deed) },
                    leadingIcon = if (completedDeeds.contains(deed)) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0B5B34),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun DailyActionSuggestionSection() {
    val actions = listOf(
        "تصدق ولو بالقليل",
        "صل ركعتي الضحى",
        "اتصل بقريب لك (صلة رحم)",
        "اقرأ صفحتين من القرآن",
        "سبح الله 100 مرة",
        "استغفر الله 100 مرة",
        "أطعم مسكيناً أو طائراً",
        "زر مريضاً إن استطعت",
        "ابتسم في وجه من تقابل",
        "أماط الأذى عن الطريق"
    )
    val dayOfYear = java.time.LocalDate.now().dayOfYear
    val action = actions[dayOfYear % actions.size]
    var isDone by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272E)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ماذا أفعل اليوم؟",
                color = Color(0xFFFFD700),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = action,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                FilterChip(
                    selected = isDone,
                    onClick = { isDone = !isDone },
                    label = { Text(if (isDone) "تمت" else "لم تتم") },
                    leadingIcon = {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF27AE60),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF2D3436),
                        labelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun NightPrayerDialog(onDismiss: () -> Unit) {
    var rakahs by remember { mutableStateOf(2) }
    var finished by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "برنامج قيام الليل", fontWeight = FontWeight.Bold) },
        text = {
            if (!finished) {
                Column {
                    Text("كم ركعة تنوي أن تصلي؟", modifier = Modifier.padding(bottom = 16.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { if (rakahs > 2) rakahs -= 2 }) { Text("-") }
                        Text(text = "$rakahs ركعات", modifier = Modifier.padding(horizontal = 16.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Button(onClick = { rakahs += 2 }) { Text("+") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { finished = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B5B34))
                    ) {
                        Text("بدء الصلاة (سأضغط هنا عند الانتهاء)")
                    }
                }
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("تقبل الله طاعتك", fontWeight = FontWeight.Bold, color = Color(0xFF0B5B34), modifier = Modifier.padding(bottom = 8.dp))
                    Text("دعاء ما بعد قيام الليل:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    Text(
                        "اللهم لك الحمد أنت نور السماوات والأرض ومن فيهن، ولك الحمد أنت قيم السماوات والأرض ومن فيهن، ولك الحمد أنت الحق، ووعدك حق، ولقاؤك حق، والجنة حق، والنار حق، والنبيون حق، ومحمد صلى الله عليه وسلم حق، والساعة حق.\n" +
                        "اللهم لك أسلمت، وبك آمنت، وعليك توكلت، وإليك أنبت، وبك خاصمت، وإليك حاكمت، فاغفر لي ما قدمت وما أخرت، وما أسررت وما أعلنت، أنت المقدم وأنت المؤخر، لا إله إلا أنت.\n" +
                        "اللهم اهدني فيمن هديت، وعافني فيمن عافيت، وتولني فيمن توليت، وبارك لي فيما أعطيت، وقني شر ما قضيت.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
fun FamilySectionDialog(onDismiss: () -> Unit) {
    val kidsDhikr = listOf("سبحان الله (33 مرة)", "الحمد لله (33 مرة)", "الله أكبر (33 مرة)", "لا إله إلا الله")
    val familyDuas = listOf("اللهم بارك في بيتنا", "اللهم احفظ أولادي", "اللهم ارزقنا البركة", "اللهم ألف بين قلوبنا")
    val morals = listOf("الصدق في الحديث", "احترام الكبير", "العطف على الصغير", "مساعدة الأم والأب")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("واحة الأسرة", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0B5B34))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "إغلاق") }
                }
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("أذكار للأطفال", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.height(8.dp))
                        kidsDhikr.forEach { dhikr ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                            ) {
                                Text(dhikr, modifier = Modifier.padding(16.dp), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Text("أدعية للأسرة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.height(8.dp))
                        familyDuas.forEach { dua ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Text(dua, modifier = Modifier.padding(16.dp), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Text("أخلاق إسلامية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.height(8.dp))
                        morals.forEach { moral ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                            ) {
                                Text(moral, modifier = Modifier.padding(16.dp), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RighteousPathDialog(onDismiss: () -> Unit) {
    var step by remember { mutableStateOf(0) } // 0: Selection, 1: Prayer, 2: Quran
    
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("طريق الاستقامة", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                if (step == 0) {
                    Text("ما هو هدفك الحالي؟", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { step = 1 }, modifier = Modifier.fillMaxWidth()) {
                        Text("أريد الالتزام بالصلاة")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth()) {
                        Text("أريد حفظ القرآن الكريم")
                    }
                } else if (step == 1) {
                    Text("خطة الالتزام بالصلاة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B5B34))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("نبدأ معك خطوة بخطوة:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    val steps = listOf(
                        "الأسبوع الأول: التركيز فقط على صلاة الفرض في أي وقت.",
                        "الأسبوع الثاني: محاولة الصلاة في أول الوقت.",
                        "الأسبوع الثالث: إضافة السنن الرواتب.",
                        "الأسبوع الرابع: المحافظة على الأذكار بعد الصلاة."
                    )
                    steps.forEachIndexed { index, text ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                            Text("${index + 1}. $text", modifier = Modifier.padding(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { step = 0 }) { Text("عودة") }
                } else if (step == 2) {
                    Text("خطة حفظ القرآن", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B5B34))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("خطوات ميسرة للحفظ:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    val steps = listOf(
                        "المرحلة الأولى: حفظ قصار السور (جزء عم).",
                        "المرحلة الثانية: حفظ سورة الملك والسجدة.",
                        "المرحلة الثالثة: حفظ 3 آيات يومياً من سورة البقرة.",
                        "المرحلة الرابعة: مراجعة ما تم حفظه يومياً."
                    )
                    steps.forEachIndexed { index, text ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                            Text("${index + 1}. $text", modifier = Modifier.padding(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { step = 0 }) { Text("عودة") }
                }
            }
        }
    }
}

@Composable
fun MentalPeaceDialog(onDismiss: () -> Unit) {
    val items = listOf(
        "لا إله إلا أنت سبحانك إني كنت من الظالمين",
        "ربي إني مسني الضر وأنت أرحم الراحمين",
        "حسبنا الله ونعم الوكيل",
        "يا حي يا قيوم برحمتك أستغيث"
    )
    
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("السكينة النفسية", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4A90E2))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("أذكار لتهدئة النفس وإزالة القلق", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { text ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Text(text, modifier = Modifier.padding(24.dp), fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium, color = Color(0xFF2C3E50))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonalWorshipDialog(onDismiss: () -> Unit) {
    var selectedSeason by remember { mutableStateOf<String?>(null) }
    
    val seasons = mapOf(
        "شهر رمضان" to listOf(
            "ختم القرآن مرة واحدة على الأقل",
            "صلاة التراويح يومياً",
            "إفطار صائم ولو بتمرة",
            "الصدقة اليومية"
        ),
        "العشر الأواخر" to listOf(
            "الاعتكاف إن تيسر",
            "تحري ليلة القدر",
            "الإكثار من الدعاء",
            "إخراج زكاة الفطر"
        ),
        "عشر ذي الحجة" to listOf(
            "الصيام (خاصة يوم عرفة)",
            "الإكثار من التكبير والتهليل",
            "الأضحية",
            "صدقة السر"
        )
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("العبادات الموسمية", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (selectedSeason == null) {
                    Text("اختر الموسم لعرض الخطة:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    seasons.keys.forEach { season ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedSeason = season },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
                        ) {
                            Text(season, modifier = Modifier.padding(20.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Text("خطة $selectedSeason", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                    Spacer(modifier = Modifier.height(16.dp))
                    seasons[selectedSeason]?.forEachIndexed { index, plan ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${index + 1}.", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(plan, fontSize = 16.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { selectedSeason = null }) {
                        Text("عودة للقائمة")
                    }
                }
            }
        }
    }
}
