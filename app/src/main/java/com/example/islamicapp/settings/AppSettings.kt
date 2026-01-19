package com.example.islamicapp.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.islamicapp.quran.Reciter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

data class SettingsState(
    val city: String = "",
    val useGps: Boolean = true,
    val reciter: Reciter = Reciter.AL_AFASY,
    val adhanEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val backgroundWorkEnabled: Boolean = true,
    val themeIndex: Int = 0,
    val fontScale: Float = 1.0f,
    val darkModeEnabled: Boolean = false,
    val languageCode: String = "ar",
    val lastReadSurah: Int = 0,
    val bookmarkedSurah: Int = 0,
    val reminderMinutes: Int = 10,
    val reminderEnabled: Boolean = true,
    val notifAdhanEnabled: Boolean = true,
    val notifAdhanMode: String = "ADHAN",
    val notifAdhanSound: String = "adhan_1",
    val notifAzkarEnabled: Boolean = true,
    val notifAzkarVoice: Boolean = true,
    val quietMode: Boolean = false,
    val azkarMorningDelay: Int = 15,
    val azkarEveningDelay: Int = 15,
    val azkarSleepTime: String = "22:00",
    val dailyIntention: String = "",
    val personalDua: String = "",
    val highPrivacyMode: Boolean = false
)

data class DailyIbadahState(
    val fajrDone: Boolean = false,
    val dhuhrDone: Boolean = false,
    val asrDone: Boolean = false,
    val maghribDone: Boolean = false,
    val ishaDone: Boolean = false,
    val quranDone: Boolean = false,
    val dhikrDone: Boolean = false,
    val nawafilDone: Boolean = false
)

object AppSettings {
    private val CITY_KEY = stringPreferencesKey("city")
    private val USE_GPS_KEY = booleanPreferencesKey("use_gps")
    private val RECITER_KEY = stringPreferencesKey("reciter")
    private val ADHAN_KEY = booleanPreferencesKey("adhan_enabled")
    private val NOTIF_KEY = booleanPreferencesKey("notifications_enabled")
    private val BG_KEY = booleanPreferencesKey("background_enabled")
    private val THEME_KEY = intPreferencesKey("theme_index")
    private val FONT_SCALE_KEY = stringPreferencesKey("font_scale")
    private val DARK_KEY = booleanPreferencesKey("dark_mode")
    private val LANG_KEY = stringPreferencesKey("language_code")
    private val LAST_READ_SURAH_KEY = intPreferencesKey("last_read_surah")
    private val BOOKMARKED_SURAH_KEY = intPreferencesKey("bookmarked_surah")
    private val REMINDER_MINUTES_KEY = intPreferencesKey("reminder_minutes")
    private val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminder_enabled")

    private val NOTIF_ADHAN_ENABLED_KEY = booleanPreferencesKey("notif_adhan_enabled")
    private val NOTIF_ADHAN_MODE_KEY = stringPreferencesKey("notif_adhan_mode")
    private val NOTIF_ADHAN_SOUND_KEY = stringPreferencesKey("notif_adhan_sound")
    private val NOTIF_AZKAR_ENABLED_KEY = booleanPreferencesKey("notif_azkar_enabled")
    private val NOTIF_AZKAR_VOICE_KEY = booleanPreferencesKey("notif_azkar_voice")
    private val QUIET_MODE_KEY = booleanPreferencesKey("quiet_mode")
    
    private val AZKAR_MORNING_DELAY_KEY = intPreferencesKey("azkar_morning_delay")
    private val AZKAR_EVENING_DELAY_KEY = intPreferencesKey("azkar_evening_delay")
    private val AZKAR_SLEEP_TIME_KEY = stringPreferencesKey("azkar_sleep_time")
    private val DAILY_INTENTION_KEY = stringPreferencesKey("daily_intention")
    private val PERSONAL_DUA_KEY = stringPreferencesKey("personal_dua")
    private val HIGH_PRIVACY_KEY = booleanPreferencesKey("high_privacy_mode")

    private val FAJR_DONE_KEY = booleanPreferencesKey("fajr_done")
    private val DHUHR_DONE_KEY = booleanPreferencesKey("dhuhr_done")
    private val ASR_DONE_KEY = booleanPreferencesKey("asr_done")
    private val MAGHRIB_DONE_KEY = booleanPreferencesKey("maghrib_done")
    private val ISHA_DONE_KEY = booleanPreferencesKey("isha_done")
    private val QURAN_DONE_KEY = booleanPreferencesKey("quran_done")
    private val DHIKR_DONE_KEY = booleanPreferencesKey("dhikr_done")
    private val NAWAFIL_DONE_KEY = booleanPreferencesKey("nawafil_done")

    fun observe(context: Context): Flow<SettingsState> {
        return context.settingsDataStore.data.map { prefs ->
            SettingsState(
                city = prefs[CITY_KEY] ?: "",
                useGps = prefs[USE_GPS_KEY] ?: true,
                reciter = prefs[RECITER_KEY]?.let { stored ->
                    Reciter.values().find { it.name == stored } ?: Reciter.AL_AFASY
                } ?: Reciter.AL_AFASY,
                adhanEnabled = prefs[ADHAN_KEY] ?: true,
                notificationsEnabled = prefs[NOTIF_KEY] ?: true,
                backgroundWorkEnabled = prefs[BG_KEY] ?: true,
                themeIndex = prefs[THEME_KEY] ?: 0,
                fontScale = prefs[FONT_SCALE_KEY]?.toFloatOrNull() ?: 1.0f,
                darkModeEnabled = prefs[DARK_KEY] ?: false,
                languageCode = prefs[LANG_KEY] ?: "ar",
                lastReadSurah = prefs[LAST_READ_SURAH_KEY] ?: 0,
                bookmarkedSurah = prefs[BOOKMARKED_SURAH_KEY] ?: 0,
                reminderMinutes = prefs[REMINDER_MINUTES_KEY] ?: 10,
                reminderEnabled = prefs[REMINDER_ENABLED_KEY] ?: true,
                notifAdhanEnabled = prefs[NOTIF_ADHAN_ENABLED_KEY] ?: true,
                notifAdhanMode = prefs[NOTIF_ADHAN_MODE_KEY] ?: "ADHAN",
                notifAdhanSound = prefs[NOTIF_ADHAN_SOUND_KEY] ?: "adhan_1",
                notifAzkarEnabled = prefs[NOTIF_AZKAR_ENABLED_KEY] ?: true,
                notifAzkarVoice = prefs[NOTIF_AZKAR_VOICE_KEY] ?: true,
                quietMode = prefs[QUIET_MODE_KEY] ?: false,
                azkarMorningDelay = prefs[AZKAR_MORNING_DELAY_KEY] ?: 15,
                azkarEveningDelay = prefs[AZKAR_EVENING_DELAY_KEY] ?: 15,
                azkarSleepTime = prefs[AZKAR_SLEEP_TIME_KEY] ?: "22:00",
                dailyIntention = prefs[DAILY_INTENTION_KEY] ?: "",
                personalDua = prefs[PERSONAL_DUA_KEY] ?: "",
                highPrivacyMode = prefs[HIGH_PRIVACY_KEY] ?: false
            )
        }
    }

    fun observeDailyIbadah(context: Context): Flow<DailyIbadahState> {
        return context.settingsDataStore.data.map { prefs ->
            DailyIbadahState(
                fajrDone = prefs[FAJR_DONE_KEY] ?: false,
                dhuhrDone = prefs[DHUHR_DONE_KEY] ?: false,
                asrDone = prefs[ASR_DONE_KEY] ?: false,
                maghribDone = prefs[MAGHRIB_DONE_KEY] ?: false,
                ishaDone = prefs[ISHA_DONE_KEY] ?: false,
                quranDone = prefs[QURAN_DONE_KEY] ?: false,
                dhikrDone = prefs[DHIKR_DONE_KEY] ?: false,
                nawafilDone = prefs[NAWAFIL_DONE_KEY] ?: false
            )
        }
    }

    suspend fun setFajrDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[FAJR_DONE_KEY] = value
        }
    }

    suspend fun setDhuhrDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[DHUHR_DONE_KEY] = value
        }
    }

    suspend fun setAsrDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[ASR_DONE_KEY] = value
        }
    }

    suspend fun setMaghribDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[MAGHRIB_DONE_KEY] = value
        }
    }

    suspend fun setIshaDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[ISHA_DONE_KEY] = value
        }
    }

    suspend fun setQuranDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[QURAN_DONE_KEY] = value
        }
    }

    suspend fun setDhikrDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[DHIKR_DONE_KEY] = value
        }
    }

    suspend fun setNawafilDone(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NAWAFIL_DONE_KEY] = value
        }
    }

    suspend fun updateCity(context: Context, city: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[CITY_KEY] = city
        }
    }

    suspend fun updateUseGps(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[USE_GPS_KEY] = value
        }
    }

    suspend fun updateReciter(context: Context, reciter: Reciter) {
        context.settingsDataStore.edit { prefs ->
            prefs[RECITER_KEY] = reciter.name
        }
    }

    suspend fun updateAdhanEnabled(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[ADHAN_KEY] = value
        }
    }

    suspend fun updateNotificationsEnabled(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIF_KEY] = value
        }
    }

    suspend fun updateBackgroundEnabled(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[BG_KEY] = value
        }
    }

    suspend fun updateThemeIndex(context: Context, index: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_KEY] = index
        }
    }

    suspend fun updateFontScale(context: Context, scale: Float) {
        context.settingsDataStore.edit { prefs ->
            prefs[FONT_SCALE_KEY] = scale.toString()
        }
    }

    suspend fun updateDarkMode(context: Context, enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[DARK_KEY] = enabled
        }
    }

    suspend fun updateLanguage(context: Context, code: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[LANG_KEY] = code
        }
    }

    suspend fun updateLastReadSurah(context: Context, surah: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[LAST_READ_SURAH_KEY] = surah
        }
    }

    suspend fun updateBookmarkedSurah(context: Context, surah: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[BOOKMARKED_SURAH_KEY] = surah
        }
    }

    suspend fun updateNotifAdhanEnabled(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIF_ADHAN_ENABLED_KEY] = value
        }
    }

    suspend fun updateNotifAdhanMode(context: Context, value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIF_ADHAN_MODE_KEY] = value
        }
    }

    suspend fun updateNotifAdhanSound(context: Context, value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIF_ADHAN_SOUND_KEY] = value
        }
    }

    suspend fun updateNotifAzkarEnabled(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIF_AZKAR_ENABLED_KEY] = value
        }
    }

    suspend fun updateNotifAzkarVoice(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIF_AZKAR_VOICE_KEY] = value
        }
    }

    suspend fun updateQuietMode(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[QUIET_MODE_KEY] = value
        }
    }

    suspend fun updateReminderMinutes(context: Context, value: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[REMINDER_MINUTES_KEY] = value
        }
    }

    suspend fun updateReminderEnabled(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[REMINDER_ENABLED_KEY] = value
        }
    }

    suspend fun updateAzkarMorningDelay(context: Context, value: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[AZKAR_MORNING_DELAY_KEY] = value
        }
    }

    suspend fun updateAzkarEveningDelay(context: Context, value: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[AZKAR_EVENING_DELAY_KEY] = value
        }
    }

    suspend fun updateAzkarSleepTime(context: Context, value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[AZKAR_SLEEP_TIME_KEY] = value
        }
    }

    suspend fun updateDailyIntention(context: Context, value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[DAILY_INTENTION_KEY] = value
        }
    }

    suspend fun updatePersonalDua(context: Context, value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[PERSONAL_DUA_KEY] = value
        }
    }

    suspend fun updateHighPrivacyMode(context: Context, value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[HIGH_PRIVACY_KEY] = value
        }
    }
}
