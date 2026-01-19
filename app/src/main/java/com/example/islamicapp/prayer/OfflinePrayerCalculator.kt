package com.example.islamicapp.prayer

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object OfflinePrayerCalculator {

    fun calculate(latitude: Double, longitude: Double): PrayerTimesUiState? {
        return try {
            val coordinates = Coordinates(latitude, longitude)
            val date = DateComponents.from(Date())

            // Choose calculation method based on location (Simplified)
            // 20-32 N, 25-36 E is roughly Egypt.
            // But for now, we can use a general method or specific if coordinates match.
            // Egypt: Lat 22-32, Long 25-35
            val params = if (latitude in 22.0..32.0 && longitude in 25.0..37.0) {
                 CalculationMethod.EGYPTIAN.parameters
            } else {
                 CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters // Default
            }
            
            params.madhab = Madhab.SHAFI

            val prayerTimes = PrayerTimes(coordinates, date, params)
            
            // Format times
            val formatter = java.text.SimpleDateFormat("HH:mm", Locale.US)
            formatter.timeZone = TimeZone.getDefault()

            // Calculate Hijri Date
            val hijriDateString = try {
                val today = LocalDate.now()
                val hijriDate = HijrahDate.from(today)
                val hijriFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ar"))
                hijriFormatter.format(hijriDate)
            } catch (e: Exception) {
                ""
            }

            PrayerTimesUiState(
                fajr = formatter.format(prayerTimes.fajr),
                sunrise = formatter.format(prayerTimes.sunrise),
                dhuhr = formatter.format(prayerTimes.dhuhr),
                asr = formatter.format(prayerTimes.asr),
                maghrib = formatter.format(prayerTimes.maghrib),
                isha = formatter.format(prayerTimes.isha),
                hijriDate = hijriDateString,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
