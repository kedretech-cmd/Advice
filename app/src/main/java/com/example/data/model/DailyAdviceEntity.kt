package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_advice")
data class DailyAdviceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val advice: String,
    val explanation: String,
    val dailyAction: String,
    val category: String,
    val categoryKey: String,
    val imagePrompt: String = "",
    val imageUrl: String? = null,
    val publicationDate: String, // YYYY-MM-DD
    val ethiopianDate: String = "",
    val isFavorite: Boolean = false,
    val isActionCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
