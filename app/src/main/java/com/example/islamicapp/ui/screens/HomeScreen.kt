package com.example.islamicapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.islamicapp.adhan.AdhanScheduler
import com.example.islamicapp.prayer.PrayerTimesUiState
import com.example.islamicapp.prayer.PrayerTimesViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: PrayerTimesViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(state.nextPrayerName, state.nextPrayerDiffMinutes) {
        if (state.nextPrayerDiffMinutes > 0) {
            AdhanScheduler.scheduleNextAdhan(context, state.nextPrayerDiffMinutes)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF062D1A), Color(0xFF0B5B34))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "السلام عليكم",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "مواقيت الصلاة لليوم",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
        )
        PrayerSection(state = state, onRetry = { viewModel.refreshTimings() })
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                title = "القرآن الكريم",
                subtitle = "تلاوة مشاري العفاسي"
            )
            FeatureCard(
                title = "السبحة الإلكترونية",
                subtitle = "عد تسبيح متقدم"
            )
        }
        FeatureCard(
            title = "أذكارك اليومية",
            subtitle = "الصباح، المساء، النوم، بعد الصلاة",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PrayerSection(state: PrayerTimesUiState, onRetry: () -> Unit) {
    if (state.isLoading) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F3B24)
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
                containerColor = Color(0xFF0F3B24)
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
            containerColor = Color(0xFF0F3B24)
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
            containerColor = Color(0xFF14402A)
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
