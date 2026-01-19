package com.example.islamicapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [QuranAyahEntity::class, TafsirAyahEntity::class],
    version = 1,
    exportSchema = true
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun dao(): QuranDao

    companion object {
        @Volatile private var INSTANCE: QuranDatabase? = null

        fun get(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_offline.db"
                )
                    // NOTE: We intentionally do NOT ship a huge DB inside the APK.
                    // The app can download + cache Quran text + tafsir once, then it works offline.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
