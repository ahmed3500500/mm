package com.example.islamicapp.data

data class SurahItem(
    val number: Int,
    val name: String,
    val englishName: String = "",
    val verseCount: Int = 0
)

data class SurahContent(
    val number: Int,
    val name: String,
    val verses: String,
    val tafsir: String
)
