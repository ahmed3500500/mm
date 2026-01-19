package com.example.islamicapp.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.islamicapp.R
import com.example.islamicapp.data.db.QuranAyahEntity
import com.example.islamicapp.data.QuranData
import com.example.islamicapp.data.SurahItem
import com.example.islamicapp.quran.QuranRepository
import com.example.islamicapp.settings.AppSettings
import com.example.islamicapp.settings.SettingsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QuranTextScreen(modifier: Modifier = Modifier) {
    var selectedSurah by remember { mutableStateOf<SurahItem?>(null) }

    BackHandler(enabled = selectedSurah != null) {
        selectedSurah = null
    }

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { QuranRepository(context) }
    val settingsFlow = remember { AppSettings.observe(context) }
    val settings by settingsFlow.collectAsState(initial = SettingsState())

    var offlineReady by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var progressText by remember { mutableStateOf("") }
    var progressValue by remember { mutableStateOf(0f) }
    var tafsirKey by remember { mutableStateOf<String?>(null) }
    var tafsirTitle by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<QuranAyahEntity>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    val surahByNumber = remember { surahs.associateBy { it.number } }

    LaunchedEffect(Unit) {
        try {
            offlineReady = withContext(Dispatchers.IO) { repo.isQuranAvailableOffline() }
            if (tafsirKey == null) {
                val (k, t) = withContext(Dispatchers.IO) { 
                    try {
                        repo.chooseDefaultArabicTafsirKey()
                    } catch (e: Exception) {
                        // Fallback in case of network error
                        "arabic_mokhtasar" to "تفسير مختصر"
                    }
                }
                tafsirKey = k
                tafsirTitle = t
            }
        } catch (e: Exception) {
            e.printStackTrace()
            offlineReady = false
        }
    }

    LaunchedEffect(searchQuery, offlineReady) {
        val q = searchQuery.trim()
        if (!offlineReady || q.length < 2) {
            searchResults = emptyList()
            isSearching = false
        } else {
            isSearching = true
            val results = withContext(Dispatchers.IO) { repo.searchAyahs(q) }
            searchResults = results
            isSearching = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "القرآن الكريم (قراءة)",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A).copy(alpha = 0.92f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (offlineReady) "جاهز بدون إنترنت ✅ (القرآن + تفسير: ${tafsirTitle ?: ""})" else "لم يتم تحميل القرآن والتفسير بعد",
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    if (!offlineReady) {
                        Button(
                            onClick = {
                                if (isDownloading) return@Button
                                isDownloading = true
                                progressValue = 0f
                                progressText = "بدء التحميل..."
                                val key = tafsirKey
                                val title = tafsirTitle
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        repo.downloadQuranText { s ->
                                            val v = s / 114f
                                            progressValue = v * 0.5f
                                            progressText = "تحميل القرآن: سورة $s/114"
                                        }
                                        if (key != null && title != null) {
                                            repo.downloadTafsir(key, title) { s ->
                                                val v = s / 114f
                                                progressValue = 0.5f + (v * 0.5f)
                                                progressText = "تحميل التفسير: سورة $s/114"
                                            }
                                        }
                                    }
                                    offlineReady = true
                                    isDownloading = false
                                    progressText = "تم التحميل ✅"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Text(text = "تحميل القرآن + التفسير للاستخدام دون إنترنت", color = Color(0xFF062D1A))
                        }

                        if (isDownloading) {
                            LinearProgressIndicator(progress = progressValue, modifier = Modifier.fillMaxWidth())
                            Text(text = progressText, color = Color(0xFFFFD700), fontSize = 12.sp)
                        }
                    }
                }
            }

            if (settings.lastReadSurah > 0) {
                val lastSurah = surahByNumber[settings.lastReadSurah]
                if (lastSurah != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3B24).copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        AppSettings.updateLastReadSurah(context, lastSurah.number)
                                    }
                                    onSurahClick(lastSurah)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "متابعة القراءة",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "آخر سورة قرأتها: ${lastSurah.name}",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        AppSettings.updateLastReadSurah(context, lastSurah.number)
                                    }
                                    onSurahClick(lastSurah)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                            ) {
                                Text(text = "متابعة", color = Color(0xFF062D1A))
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                label = { Text(text = "بحث سريع داخل القرآن (أدخل كلمتين على الأقل)") }
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
                    if (searchQuery.trim().length >= 2 && offlineReady) {
                        if (isSearching) {
                            item {
                                Text(
                                    text = "جارِ البحث...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (searchResults.isEmpty()) {
                            item {
                                Text(
                                    text = "لا توجد نتائج مطابقة للنص المدخل.",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(searchResults) { ayah ->
                                val surahItem = surahByNumber[ayah.surah] ?: SurahItem(ayah.surah, "سورة ${ayah.surah}")
                                SearchResultRow(
                                    ayah = ayah,
                                    surah = surahItem,
                                    onClick = {
                                        scope.launch {
                                            AppSettings.updateLastReadSurah(context, surahItem.number)
                                        }
                                        onSurahClick(surahItem)
                                    }
                                )
                            }
                        }
                    } else {
                        items(surahs) { surah ->
                            val isBookmark = surah.number == settings.bookmarkedSurah
                            val isLast = surah.number == settings.lastReadSurah
                            SurahTextRow(
                                surah = surah,
                                isBookmarked = isBookmark,
                                isLastRead = isLast,
                                onClick = {
                                    scope.launch {
                                        AppSettings.updateLastReadSurah(context, surah.number)
                                    }
                                    onSurahClick(surah)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahTextRow(
    surah: SurahItem,
    isBookmarked: Boolean,
    isLastRead: Boolean,
    onClick: () -> Unit
) {
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
                if (isBookmarked) {
                    Text(
                        text = "علامة مرجعية",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp
                    )
                } else if (isLastRead) {
                    Text(
                        text = "آخر موضع قراءة",
                        color = Color(0xFFB0FFB0),
                        fontSize = 11.sp
                    )
                }
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
fun SearchResultRow(
    ayah: QuranAyahEntity,
    surah: SurahItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14402A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${surah.name} - آية ${ayah.ayah}",
                color = Color(0xFFFFD700),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ayah.text,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SurahDetailView(
    surahItem: SurahItem,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { QuranRepository(context) }
    val scope = rememberCoroutineScope()
    val settingsFlow = remember { AppSettings.observe(context) }
    val settings by settingsFlow.collectAsState(initial = SettingsState())
    var ayahsText by remember(surahItem.number) { mutableStateOf<List<String>>(emptyList()) }
    var tafsirText by remember(surahItem.number) { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var showTafsir by remember { mutableStateOf(true) }
    var offlineReady by remember { mutableStateOf(false) }
    var tafsirTitle by remember { mutableStateOf<String?>(null) }
    var tafsirKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(surahItem.number) {
        try {
            offlineReady = withContext(Dispatchers.IO) { repo.isQuranAvailableOffline() }
            val (k, t) = withContext(Dispatchers.IO) { 
                try {
                    repo.chooseDefaultArabicTafsirKey()
                } catch (e: Exception) {
                    "arabic_mokhtasar" to "تفسير مختصر"
                }
            }
            tafsirKey = k
            tafsirTitle = t
            if (offlineReady) {
                val ayahs = withContext(Dispatchers.IO) { repo.getSurahAyahs(surahItem.number) }
                ayahsText = ayahs.sortedBy { it.ayah }.map { "${it.ayah}. ${it.text}" }
                val tfs = withContext(Dispatchers.IO) {
                    try {
                        repo.getTafsirForSurah(k, surahItem.number)
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                tafsirText = tfs.associate { it.ayah to it.text }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val isBookmarked = settings.bookmarkedSurah == surahItem.number

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
                IconButton(
                    onClick = {
                        scope.launch {
                            val target = if (isBookmarked) 0 else surahItem.number
                            AppSettings.updateBookmarkedSurah(context, target)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isBookmarked) Color(0xFFFFD700) else Color.White
                    )
                }
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
                    if (!offlineReady) {
                        item {
                            Text(
                                text = "لا يوجد قرآن/تفسير محمّل للاستخدام دون إنترنت. ارجع للشاشة السابقة واضغط: تحميل القرآن + التفسير.",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "التفسير: ${tafsirTitle ?: ""}",
                                    color = Color(0xFF14402A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Button(
                                    onClick = { showTafsir = !showTafsir },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14402A))
                                ) {
                                    Text(text = if (showTafsir) "إخفاء التفسير" else "إظهار التفسير", color = Color.White)
                                }
                            }
                        }

                        items(ayahsText) { line ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color.Black,
                                        lineHeight = 30.sp,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (showTafsir) {
                                    val ayahNo = line.substringBefore('.').toIntOrNull()
                                    val tafsir = if (ayahNo != null) tafsirText[ayahNo] else null
                                    if (!tafsir.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = tafsir,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color(0xFF14402A),
                                                lineHeight = 22.sp,
                                                fontSize = 15.sp
                                            ),
                                            textAlign = TextAlign.Start,
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
    }
}
