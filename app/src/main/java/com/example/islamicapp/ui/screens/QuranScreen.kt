package com.example.islamicapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.quran.QuranPlayer

data class SurahItem(
    val number: Int,
    val name: String
)

@Composable
fun QuranScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val surahs = remember {
        (1..114).map { number ->
            SurahItem(number, "سورة $number")
        }
    }
    var current by remember { mutableStateOf<SurahItem?>(null) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF062D1A), Color(0xFF0B5B34))
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "القرآن الكريم",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color(0xFFFFD700)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (current != null) {
            CurrentSurahControls(
                surah = current!!,
                onStop = {
                    QuranPlayer.stop()
                },
                onNext = {
                    val nextNumber = if (current!!.number >= 114) 1 else current!!.number + 1
                    val next = SurahItem(nextNumber, "سورة $nextNumber")
                    current = next
                    QuranPlayer.playSurah(context, next.number)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24)),
            shape = RoundedCornerShape(24.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(surahs) { surah ->
                    SurahRow(
                        surah = surah,
                        onPlay = {
                            current = surah
                            QuranPlayer.playSurah(context, surah.number)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentSurahControls(
    surah: SurahItem,
    onStop: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "يتم تشغيل تلاوة مشاري العفاسي",
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = surah.name,
                color = Color(0xFFFFD700),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "إيقاف", color = Color(0xFF062D1A))
                }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "السورة التالية", color = Color(0xFF062D1A))
                }
            }
        }
    }
}

@Composable
fun SurahRow(surah: SurahItem, onPlay: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlay() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = surah.name, color = Color.White)
                Text(
                    text = "مشاري العفاسي",
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(text = "تشغيل", color = Color(0xFF062D1A))
            }
        }
    }
}

