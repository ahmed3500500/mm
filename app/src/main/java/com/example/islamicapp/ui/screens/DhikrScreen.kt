package com.example.islamicapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.R

@Composable
fun DhikrScreen(modifier: Modifier = Modifier) {
    val azkarList = listOf(
        DhikrCategory(
            "أذكار الصباح",
            listOf(
                DhikrItem("آية الكرسي", "الله لا إله إلا هو الحي القيوم...", 1),
                DhikrItem("سيد الاستغفار", "اللهم أنت ربي لا إله إلا أنت...", 1),
                DhikrItem("الحمد لله", "الحمد لله الذي أحيانا بعد ما أماتنا وإليه النشور", 1),
                DhikrItem("بسم الله", "بسم الله الذي لا يضر مع اسمه شيء في الأرض ولا في السماء...", 3)
            )
        ),
        DhikrCategory(
            "أذكار المساء",
            listOf(
                DhikrItem("آية الكرسي", "الله لا إله إلا هو الحي القيوم...", 1),
                DhikrItem("سيد الاستغفار", "اللهم أنت ربي لا إله إلا أنت...", 1),
                DhikrItem("أمسينا وأمسى الملك لله", "أمسينا وأمسى الملك لله والحمد لله...", 1),
                DhikrItem("أعوذ بكلمات الله", "أعوذ بكلمات الله التامات من شر ما خلق", 3)
            )
        ),
        DhikrCategory(
            "أذكار بعد الصلاة",
            listOf(
                DhikrItem("الاستغفار", "أستغفر الله، أستغفر الله، أستغفر الله", 3),
                DhikrItem("التسبيح", "سبحان الله", 33),
                DhikrItem("التحميد", "الحمد لله", 33),
                DhikrItem("التكبير", "الله أكبر", 33),
                DhikrItem("التهليل", "لا إله إلا الله وحده لا شريك له...", 1)
            )
        ),
        DhikrCategory(
            "أذكار النوم",
            listOf(
                DhikrItem("باسمك اللهم", "باسمك اللهم أموت وأحيا", 1),
                DhikrItem("آية الكرسي", "الله لا إله إلا هو الحي القيوم...", 1),
                DhikrItem("سورة الإخلاص والمعوذتين", "قل هو الله أحد...", 3)
            )
        )
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
                text = "الأذكار اليومية",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(azkarList) { category ->
                    DhikrCategoryCard(category)
                }
            }
        }
    }
}

@Composable
fun DhikrCategoryCard(category: DhikrCategory) {
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
                text = category.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
            )
            
            category.items.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F3B24).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "${item.count} مرة",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFFFD700)
                                )
                            )
                        }
                        if (item.text.isNotEmpty()) {
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class DhikrCategory(
    val title: String,
    val items: List<DhikrItem>
)

data class DhikrItem(
    val title: String,
    val text: String,
    val count: Int
)
