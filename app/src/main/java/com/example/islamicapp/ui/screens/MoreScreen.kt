package com.example.islamicapp.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.qibla.QiblaCalculator
import com.example.islamicapp.tasbeeh.TasbeehPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DhikrItem(
    val title: String,
    val text: String
)

@Composable
fun MoreScreen(modifier: Modifier = Modifier) {
    val scroll = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF062D1A), Color(0xFF0B5B34))
                )
            )
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "الأدوات الإسلامية",
            color = Color(0xFFFFD700),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TasbeehSection()
        MorningEveningDhikrSection()
        QiblaSection()
        NamesOfAllahSection()
        SettingsPreviewSection()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TasbeehSection() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var count by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        count = withContext(Dispatchers.IO) { TasbeehPreferences.getCount(context) }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "السبحة الإلكترونية",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "حفظ تلقائي للعدد مع اهتزاز عند 33 و 100",
                color = Color.White,
                fontSize = 12.sp
            )
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val newCount = count + 1
                        count = newCount
                        if (newCount % 33 == 0 || newCount % 100 == 0) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        saveTasbeehAsync(context, newCount)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(text = "سبح", color = Color(0xFF062D1A), fontSize = 16.sp)
                }
                Button(
                    onClick = {
                        count = 0
                        saveTasbeehAsync(context, 0)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B5B34))
                ) {
                    Text(text = "إعادة التعيين", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun MorningEveningDhikrSection() {
    val items = listOf(
        DhikrItem(
            "ذكر الصباح",
            "أصبحنا وأصبح الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير."
        ),
        DhikrItem(
            "ذكر المساء",
            "أمسينا وأمسى الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير."
        ),
        DhikrItem(
            "ذكر النوم",
            "باسمك اللهم أحيا وأموت."
        ),
        DhikrItem(
            "ذكر بعد الصلاة",
            "سبحان الله 33 مرة، والحمد لله 33 مرة، والله أكبر 34 مرة."
        )
    )
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
                text = "أذكارك اليومية",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            items.forEach { item ->
                Text(
                    text = item.title,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = item.text,
                    color = Color.White,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun NamesOfAllahSection() {
    val names = listOf(
        "الله",
        "الرحمن",
        "الرحيم",
        "الملك",
        "القدوس",
        "السلام",
        "المؤمن",
        "المهيمن",
        "العزيز",
        "الجبار",
        "المتكبر",
        "الخالق",
        "البارئ",
        "المصور",
        "الغفار",
        "القهار",
        "الوهاب",
        "الرزاق",
        "الفتاح",
        "العليم"
    )
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
                text = "أسماء الله الحسنى",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "عرض مختصر لبعض الأسماء مع تصميم أنيق. يمكنك إكمال باقي الأسماء بسهولة بنفس الأسلوب.",
                color = Color.White,
                fontSize = 13.sp
            )
            names.forEach { name ->
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsPreviewSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "الإعدادات السريعة",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "اختيار المدينة، وضع ليلي/نهاري، حجم الخط، واختيار القارئ المفضل سيتم بناؤها في شاشة مستقلة يمكن ربطها من هنا.",
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

private fun saveTasbeehAsync(context: Context, value: Int) {
    kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
        TasbeehPreferences.saveCount(context, value)
    }
}

@Composable
private fun QiblaSection() {
    val context = LocalContext.current
    var bearing by remember { mutableStateOf<Float?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        try {
            bearing = withContext(Dispatchers.IO) {
                QiblaCalculator.getQiblaBearing(context)
            }
        } catch (e: Exception) {
            error = "تعذر الحصول على الموقع لتحديد اتجاه القبلة"
        }
    }
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
                text = "اتجاه القبلة",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            if (bearing != null) {
                Text(
                    text = "اتجه نحو الزاوية ${bearing?.toInt()}° تقريباً من الشمال الحقيقي",
                    color = Color.White,
                    fontSize = 13.sp
                )
            } else if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color.White,
                    fontSize = 13.sp
                )
            } else {
                Text(
                    text = "جاري تحديد اتجاه القبلة حسب موقعك الحالي",
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }
    }
}
