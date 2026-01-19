package com.example.islamicapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun NamesScreen(modifier: Modifier = Modifier) {
    val names = listOf(
        "الله", "الرحمن", "الرحيم", "الملك", "القدوس", "السلام", "المؤمن", "المهيمن", "العزيز", "الجبار",
        "المتكبر", "الخالق", "البارئ", "المصور", "الغفار", "القهار", "الوهاب", "الرزاق", "الفتاح", "العليم",
        "القابض", "الباسط", "الخافض", "الرافع", "المعز", "المذل", "السميع", "البصير", "الحكيم", "العدل",
        "اللطيف", "الخبير", "الحليم", "العظيم", "الغفور", "الشكور", "العلي", "الكبير", "الحفيظ", "المقيت",
        "الحسيب", "الجليل", "الكريم", "الرقيب", "المجيب", "الواسع", "الحكيم", "الودود", "المجيد", "الباعث",
        "الشهيد", "الحق", "الوكيل", "القوي", "المتين", "الولي", "الحميد", "المحصي", "المبدئ", "المعيد",
        "المحيي", "المميت", "الحي", "القيوم", "الواجد", "الماجد", "الواحد", "الأحد", "الصمد", "القادر",
        "المقتدر", "المقدم", "المؤخر", "الأول", "الآخر", "الظاهر", "الباطن", "الوالي", "المتعالي", "البر",
        "التواب", "المنتقم", "العفو", "الرؤوف", "مالك الملك", "ذو الجلال والإكرام", "المقسط", "الجامع",
        "الغني", "المغني", "المانع", "الضار", "النافع", "النور", "الهادي", "البديع", "الباقي", "الوارث",
        "الرشيد", "الصبور"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "أسماء الله الحسنى",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(names) { name ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF14402A).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(100.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
