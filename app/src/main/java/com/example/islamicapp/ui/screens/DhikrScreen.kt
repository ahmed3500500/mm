package com.example.islamicapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.R
import com.example.islamicapp.settings.AppSettings
import kotlinx.coroutines.launch

@Composable
fun DhikrScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by AppSettings.observe(context).collectAsState(
        initial = com.example.islamicapp.settings.SettingsState()
    )
    var personalDua by remember(settings.personalDua) { mutableStateOf(settings.personalDua) }

    val azkarList = listOf(
        DhikrCategory(
            "أذكار الصباح",
            listOf(
                DhikrEntry("آية الكرسي", "الله لا إله إلا هو الحي القيوم...", 1),
                DhikrEntry("سيد الاستغفار", "اللهم أنت ربي لا إله إلا أنت...", 1),
                DhikrEntry("الحمد لله", "الحمد لله الذي أحيانا بعد ما أماتنا وإليه النشور", 1),
                DhikrEntry("بسم الله", "بسم الله الذي لا يضر مع اسمه شيء في الأرض ولا في السماء...", 3)
            )
        ),
        DhikrCategory(
            "أذكار المساء",
            listOf(
                DhikrEntry("آية الكرسي", "الله لا إله إلا هو الحي القيوم...", 1),
                DhikrEntry("سيد الاستغفار", "اللهم أنت ربي لا إله إلا أنت...", 1),
                DhikrEntry("أمسينا وأمسى الملك لله", "أمسينا وأمسى الملك لله والحمد لله...", 1),
                DhikrEntry("أعوذ بكلمات الله", "أعوذ بكلمات الله التامات من شر ما خلق", 3)
            )
        ),
        DhikrCategory(
            "أذكار بعد الصلاة",
            listOf(
                DhikrEntry("الاستغفار", "أستغفر الله، أستغفر الله، أستغفر الله", 3),
                DhikrEntry("التسبيح", "سبحان الله", 33),
                DhikrEntry("التحميد", "الحمد لله", 33),
                DhikrEntry("التكبير", "الله أكبر", 33),
                DhikrEntry("التهليل", "لا إله إلا الله وحده لا شريك له...", 1)
            )
        ),
        DhikrCategory(
            "أذكار النوم",
            listOf(
                DhikrEntry("باسمك اللهم", "باسمك اللهم أموت وأحيا", 1),
                DhikrEntry("آية الكرسي", "الله لا إله إلا هو الحي القيوم...", 1),
                DhikrEntry("سورة الإخلاص والمعوذتين", "قل هو الله أحد...", 3)
            )
        ),
        DhikrCategory(
            "أذكار عند الاستيقاظ",
            listOf(
                DhikrEntry("الحمد لله الذي أحيانا", "الحمد لله الذي أحيانا بعد ما أماتنا وإليه النشور", 1)
            )
        ),
        DhikrCategory(
            "أذكار دخول وخروج المنزل",
            listOf(
                DhikrEntry("دخول المنزل", "بسم الله، اللهم إني أسألك خير المولج وخير المخرج، بسم الله ولجنا وبسم الله خرجنا وعلى الله ربنا توكلنا", 1),
                DhikrEntry("الخروج من المنزل", "بسم الله، توكلت على الله، ولا حول ولا قوة إلا بالله", 1)
            )
        ),
        DhikrCategory(
            "أذكار الطعام",
            listOf(
                DhikrEntry("قبل الطعام", "بسم الله", 1),
                DhikrEntry("بعد الطعام", "الحمد لله الذي أطعمني هذا ورزقنيه من غير حول مني ولا قوة", 1)
            )
        ),
        DhikrCategory(
            "أذكار السفر",
            listOf(
                DhikrEntry("دعاء السفر", "اللهم إنا نسألك في سفرنا هذا البر والتقوى، ومن العمل ما ترضى، اللهم هون علينا سفرنا هذا واطو عنا بعده", 1)
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
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "الأدعية المختارة",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(duaCategories) { category ->
                    DuaCategoryCard(category)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                text = "دعاؤك الخاص",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            OutlinedTextField(
                                value = personalDua,
                                onValueChange = { personalDua = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                placeholder = {
                                    Text(
                                        text = "اكتب دعاءك هنا...",
                                        fontSize = 12.sp
                                    )
                                }
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        AppSettings.updatePersonalDua(context, personalDua)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color(0xFF062D1A)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "حفظ الدعاء", fontSize = 14.sp)
                            }
                            if (settings.personalDua.isNotBlank()) {
                                Text(
                                    text = "آخر نسخة محفوظة:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                                Text(
                                    text = settings.personalDua,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
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
    val items: List<DhikrEntry>
)

data class DhikrEntry(
    val title: String,
    val text: String,
    val count: Int
)

data class DuaCategory(
    val title: String,
    val items: List<DuaItem>
)

data class DuaItem(
    val title: String,
    val text: String
)

private val duaCategories = listOf(
    DuaCategory(
        "أدعية الضيق والهم",
        listOf(
            DuaItem(
                "اللهم إني أعوذ بك من الهم",
                "اللهم إني أعوذ بك من الهم والحزن، وأعوذ بك من العجز والكسل، وأعوذ بك من الجبن والبخل، وأعوذ بك من غلبة الدين وقهر الرجال."
            ),
            DuaItem(
                "يا حي يا قيوم",
                "يا حي يا قيوم برحمتك أستغيث، أصلح لي شأني كله ولا تكلني إلى نفسي طرفة عين."
            )
        )
    ),
    DuaCategory(
        "أدعية الخوف",
        listOf(
            DuaItem(
                "لا إله إلا الله العظيم الحليم",
                "لا إله إلا الله العظيم الحليم، لا إله إلا الله رب العرش العظيم، لا إله إلا الله رب السماوات ورب الأرض ورب العرش الكريم."
            )
        )
    ),
    DuaCategory(
        "أدعية الرزق",
        listOf(
            DuaItem(
                "اللهم اكفني بحلالك",
                "اللهم اكفني بحلالك عن حرامك وأغنني بفضلك عمن سواك."
            ),
            DuaItem(
                "يا رزاق",
                "يا رزاق يا كريم، ارزقني رزقًا حلالًا طيبًا مباركًا فيه."
            )
        )
    ),
    DuaCategory(
        "أدعية المرض",
        listOf(
            DuaItem(
                "اللهم رب الناس",
                "اللهم رب الناس، أذهب البأس، اشفِ أنت الشافي، لا شفاء إلا شفاؤك، شفاءً لا يغادر سقمًا."
            )
        )
    ),
    DuaCategory(
        "أدعية السفر",
        listOf(
            DuaItem(
                "دعاء السفر",
                "اللهم أنت الصاحب في السفر والخليفة في الأهل، اللهم إني أعوذ بك من وعثاء السفر وكآبة المنظر وسوء المنقلب في المال والأهل."
            )
        )
    ),
    DuaCategory(
        "دعاء الاستخارة",
        listOf(
            DuaItem(
                "دعاء الاستخارة",
                "اللهم إني أستخيرك بعلمك، وأستقدرك بقدرتك، وأسألك من فضلك العظيم، فإنك تقدر ولا أقدر، وتعلم ولا أعلم، وأنت علام الغيوب..."
            )
        )
    )
)

@Composable
fun DuaCategoryCard(category: DuaCategory) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
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
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    clipboard.setText(AnnotatedString(item.text))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color(0xFF062D1A)
                                )
                            ) {
                                Text(text = "نسخ الدعاء", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, item.text)
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "مشاركة الدعاء")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0B5B34),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(text = "مشاركة", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
