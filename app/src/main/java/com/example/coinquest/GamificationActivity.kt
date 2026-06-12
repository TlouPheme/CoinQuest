package com.example.coinquest

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.AppDatabase
import kotlinx.coroutines.launch

class GamificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification)

        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val tvXp = findViewById<TextView>(R.id.tvXp)
        val pbXp = findViewById<ProgressBar>(R.id.pbXp)
        val rvBadges = findViewById<RecyclerView>(R.id.rvBadges)

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            db.achievementDao().getUserProgress().collect { progress ->
                progress?.let {
                    tvLevel.text = "Level ${it.level}"
                    val currentXpInLevel = it.xp % 100
                    tvXp.text = "$currentXpInLevel / 100 XP"
                    pbXp.progress = currentXpInLevel
                }
            }
        }

        lifecycleScope.launch {
            db.achievementDao().getAllBadges().collect { badges ->
                rvBadges.adapter = BadgeAdapter(badges)
            }
        }
    }
}
