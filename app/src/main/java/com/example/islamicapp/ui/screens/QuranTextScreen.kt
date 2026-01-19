package com.example.islamicapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.R
import com.example.islamicapp.data.QuranData
import com.example.islamicapp.data.SurahItem

@Composable
fun QuranTextScreen(modifier: Modifier = Modifier) {
    var selectedSurah by remember { mutableStateOf<SurahItem?>(null) }

    if (selectedSurah != null) {
        SurahDetailView(
            surahItem = selectedSurah!!,
            onBack = { selectedSurah = null }
        )
    } else {
        SurahListView(
            onSurahClick = { selectedSurah = it },
            modifier = modifier
        )
    }
}

@Composable
fun SurahListView(
    onSurahClick: (SurahItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val surahs = remember { QuranData.getSurahList() }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "القرآن الكريم (قراءة وتفسير)",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24).copy(alpha = 0.9f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(surahs) { surah ->
                        SurahTextRow(
                            surah = surah,
                            onClick = { onSurahClick(surah) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahTextRow(surah: SurahItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = surah.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = "رقم السورة: ${surah.number}",
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(text = "قراءة", color = Color(0xFF062D1A))
            }
        }
    }
}

@Composable
fun SurahDetailView(
    surahItem: SurahItem,
    onBack: () -> Unit
) {
    val content = remember(surahItem.number) { QuranData.getSurahContent(surahItem.number) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = surahItem.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(0.2f))
            }

            Card(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF5E6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = content.verses,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black,
                                lineHeight = 30.sp,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F3B24).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "--- التفسير ---",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F3B24)
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                )
                                Text(
                                    text = content.tafsir,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.DarkGray,
                                        lineHeight = 24.sp,
                                        fontSize = 16.sp
                                    ),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
