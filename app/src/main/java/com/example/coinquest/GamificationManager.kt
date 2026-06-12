package com.example.coinquest

import android.content.Context
import com.example.coinquest.data.AppDatabase
import com.example.coinquest.data.Badge
import com.example.coinquest.data.UserProgress
import kotlinx.coroutines.flow.first

class GamificationManager(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.achievementDao()

    suspend fun addTransactionPoints() {
        var progress = dao.getUserProgress().first() ?: UserProgress()
        
        val newXp = progress.xp + 10
        val newPoints = progress.totalPoints + 5
        val newLevel = (newXp / 100) + 1
        
        // Handle consecutive days
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val lastDay = progress.lastLoggedTimestamp / (1000 * 60 * 60 * 24)
        
        var newConsecutive = progress.consecutiveDays
        if (today == lastDay + 1) {
            newConsecutive++
            checkConsecutiveBadges(newConsecutive)
        } else if (today > lastDay + 1) {
            newConsecutive = 1
        }
        
        progress = progress.copy(
            xp = newXp,
            totalPoints = newPoints,
            level = newLevel,
            consecutiveDays = newConsecutive,
            lastLoggedTimestamp = System.currentTimeMillis()
        )
        
        dao.updateProgress(progress)
        checkLevelBadges(newLevel)
    }

    private suspend fun checkConsecutiveBadges(days: Int) {
        if (days == 7) {
            awardBadge("Weekly Warrior", "Logged expenses for 7 consecutive days!", android.R.drawable.ic_menu_today)
        }
    }

    private suspend fun checkLevelBadges(level: Int) {
        when (level) {
            2 -> awardBadge("Beginner", "Reached Level 2!", android.R.drawable.star_on)
            5 -> awardBadge("Saver", "Reached Level 5!", android.R.drawable.star_on)
            10 -> awardBadge("Budget Master", "Reached Level 10!", android.R.drawable.star_on)
        }
    }

    private suspend fun awardBadge(name: String, desc: String, icon: Int) {
        val badges = dao.getAllBadges().first()
        if (badges.none { it.name == name }) {
            dao.insertBadge(Badge(name = name, description = desc, iconResource = icon, dateEarned = System.currentTimeMillis()))
        }
    }
}
