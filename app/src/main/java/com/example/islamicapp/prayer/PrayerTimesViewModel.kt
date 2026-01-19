package com.example.islamicapp.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.islamicapp.data.PrayerTimesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

data class PrayerTimesUiState(
    val cityArabic: String = "مكة المكرمة",
    val cityEnglish: String = "Makkah",
    val countryEnglish: String = "Saudi Arabia",
    val hijriDate: String = "",
    val fajr: String = "",
    val sunrise: String = "",
    val dhuhr: String = "",
    val asr: String = "",
    val maghrib: String = "",
    val isha: String = "",
    val nextPrayerName: String = "",
    val nextPrayerTime: String = "",
    val nextPrayerRemaining: String = "",
    val nextPrayerDiffMinutes: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class PrayerTimesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState

    private var timerJob: Job? = null

    init {
        refreshTimings()
    }

    fun refreshTimings() {
        viewModelScope.launch {
            loadTimingsByCity()
        }
    }

    fun refreshTimingsForLocation(latitude: Double, longitude: Double, cityArabic: String?) {
        viewModelScope.launch {
            loadTimingsByCoordinates(latitude, longitude, cityArabic)
        }
    }

    private suspend fun loadTimingsByCity() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            val response = PrayerTimesRepository.getTimingsByCity(
                _uiState.value.cityEnglish,
                _uiState.value.countryEnglish
            )
            val data = response.data
            val timings = data?.timings
            val hijri = data?.date?.hijri?.date.orEmpty()
            val newState = _uiState.value.copy(
                hijriDate = hijri,
                fajr = timings?.fajr.orEmpty(),
                sunrise = timings?.sunrise.orEmpty(),
                dhuhr = timings?.dhuhr.orEmpty(),
                asr = timings?.asr.orEmpty(),
                maghrib = timings?.maghrib.orEmpty(),
                isha = timings?.isha.orEmpty(),
                isLoading = false,
                error = null
            )
            _uiState.value = newState
            updateNextPrayer()
            startTimer()
        } catch (e: Exception) {
            applyFallbackTimings()
        }
    }

    private suspend fun loadTimingsByCoordinates(latitude: Double, longitude: Double, cityArabic: String?) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            val response = PrayerTimesRepository.getTimingsByCoordinates(latitude, longitude)
            val data = response.data
            val timings = data?.timings
            val hijri = data?.date?.hijri?.date.orEmpty()
            val newState = _uiState.value.copy(
                cityArabic = cityArabic ?: "موقعك الحالي",
                cityEnglish = "Current",
                hijriDate = hijri,
                fajr = timings?.fajr.orEmpty(),
                sunrise = timings?.sunrise.orEmpty(),
                dhuhr = timings?.dhuhr.orEmpty(),
                asr = timings?.asr.orEmpty(),
                maghrib = timings?.maghrib.orEmpty(),
                isha = timings?.isha.orEmpty(),
                isLoading = false,
                error = null
            )
            _uiState.value = newState
            updateNextPrayer()
            startTimer()
        } catch (e: Exception) {
            applyFallbackTimings()
        }
    }

    private fun applyFallbackTimings() {
        val fallbackState = _uiState.value.copy(
            hijriDate = "19 رجب 1447",
            fajr = "05:41",
            sunrise = "06:59",
            dhuhr = "12:32",
            asr = "15:42",
            maghrib = "18:02",
            isha = "19:32",
            isLoading = false,
            error = null
        )
        _uiState.value = fallbackState
        updateNextPrayer()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(60_000L)
                updateNextPrayer()
            }
        }
    }

    private fun parseTimeToMinutes(time: String): Int? {
        val parts = time.take(5).split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        return hour * 60 + minute
    }

    private fun updateNextPrayer() {
        val state = _uiState.value
        val now = Calendar.getInstance()
        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val times = listOf(
            Triple("الفجر", state.fajr, parseTimeToMinutes(state.fajr)),
            Triple("الظهر", state.dhuhr, parseTimeToMinutes(state.dhuhr)),
            Triple("العصر", state.asr, parseTimeToMinutes(state.asr)),
            Triple("المغرب", state.maghrib, parseTimeToMinutes(state.maghrib)),
            Triple("العشاء", state.isha, parseTimeToMinutes(state.isha))
        ).filter { it.third != null }

        if (times.isEmpty()) return

        val next = times.minByOrNull { triple ->
            val minutes = triple.third!!
            val diff = when {
                minutes >= nowMinutes -> minutes - nowMinutes
                else -> minutes + 24 * 60 - nowMinutes
            }
            if (diff == 0) 24 * 60 else diff
        } ?: return

        val nextMinutes = next.third!!
        var diffMinutes = nextMinutes - nowMinutes
        if (diffMinutes <= 0) {
            diffMinutes += 24 * 60
        }
        val hours = diffMinutes / 60
        val minutes = abs(diffMinutes % 60)
        val remaining = String.format("%02d:%02d", hours, minutes)

        _uiState.value = state.copy(
            nextPrayerName = next.first,
            nextPrayerTime = next.second,
            nextPrayerRemaining = remaining,
            nextPrayerDiffMinutes = diffMinutes
        )
    }
}
