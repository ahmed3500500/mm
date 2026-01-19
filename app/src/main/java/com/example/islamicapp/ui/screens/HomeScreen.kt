package com.example.islamicapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.islamicapp.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.islamicapp.adhan.AdhanScheduler
import com.example.islamicapp.prayer.PrayerTimesUiState
import com.example.islamicapp.prayer.PrayerTimesViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: PrayerTimesViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var now by remember { mutableStateOf(LocalDateTime.now(ZoneId.systemDefault())) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now(ZoneId.systemDefault())
            kotlinx.coroutines.delay(1000L)
        }
    }
    LaunchedEffect(state.nextPrayerName, state.nextPrayerDiffMinutes) {
        if (state.nextPrayerDiffMinutes > 0) {
            AdhanScheduler.scheduleNextAdhan(context, state.nextPrayerDiffMinutes)
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Content Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val timeText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val dateGregorian = now.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            
            // Header Section
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
            
            // Navigation Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FeatureCard(title = "القرآن الكريم", subtitle = "تلاوة مشاري العفاسي")
                }
                item {
                    FeatureCard(title = "السبحة الإلكترونية", subtitle = "عد تسبيح متقدم")
                }
                item {
                    FeatureCard(title = "أذكارك اليومية", subtitle = "الصباح، المساء، النوم")
                }
                item {
                    FeatureCard(title = "اتجاه القبلة", subtitle = "تحديد القبلة بدقة")
                }
                item {
                    FeatureCard(title = "أسماء الله الحسنى", subtitle = "شرح 99 اسم")
                }
                item {
                    FeatureCard(title = "الإعدادات", subtitle = "المدينة، الصوت، الثيم")
                }
            }
        }
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "الفجر", color = Color(0xFFFFD700))
                    Text(text = state.fajr, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "الظهر", color = Color(0xFFFFD700))
                    Text(text = state.dhuhr, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "العصر", color = Color(0xFFFFD700))
                    Text(text = state.asr, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "المغرب", color = Color(0xFFFFD700))
                    Text(text = state.maghrib, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "العشاء", color = Color(0xFFFFD700))
                    Text(text = state.isha, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
