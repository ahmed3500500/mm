package com.example.islamicapp.dhikr

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dhikrDataStore by preferencesDataStore(name = "dhikr_prefs")

object DhikrPreferences {
    private fun keyFor(id: String) = intPreferencesKey("count_$id")

    suspend fun getCount(context: Context, id: String): Int {
        val prefs = context.dhikrDataStore.data.first()
        return prefs[keyFor(id)] ?: 0
    }

    suspend fun saveCount(context: Context, id: String, value: Int) {
        context.dhikrDataStore.edit { prefs ->
            prefs[keyFor(id)] = value
        }
    }
}

