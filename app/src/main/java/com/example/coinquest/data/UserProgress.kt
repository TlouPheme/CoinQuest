package com.example.coinquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1, // Only one record for the user
    val totalPoints: Int = 0,
    val xp: Int = 0,
    val level: Int = 1,
    val consecutiveDays: Int = 0,
    val lastLoggedTimestamp: Long = 0
)
