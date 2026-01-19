package com.example.islamicapp.tasbeeh

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.tasbeehDataStore by preferencesDataStore(name = "tasbeeh_prefs")

object TasbeehPreferences {
    private val COUNT_KEY = intPreferencesKey("count")

    suspend fun getCount(context: Context): Int {
        val prefs = context.tasbeehDataStore.data.first()
        return prefs[COUNT_KEY] ?: 0
    }

    suspend fun saveCount(context: Context, value: Int) {
        context.tasbeehDataStore.edit { prefs ->
            prefs[COUNT_KEY] = value
        }
    }
}

