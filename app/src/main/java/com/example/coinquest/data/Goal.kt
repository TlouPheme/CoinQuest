package com.example.coinquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val type: String, // "MAX_EXPENSE" or "MIN_INCOME"
    val targetAmount: Double
)
