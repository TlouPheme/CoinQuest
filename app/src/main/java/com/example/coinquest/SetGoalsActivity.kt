package com.example.coinquest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinquest.data.AppDatabase
import com.example.coinquest.data.Goal
import kotlinx.coroutines.launch

class SetGoalsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_goals)

        val etMinIncome = findViewById<EditText>(R.id.etMinIncome)
        val etMaxExpense = findViewById<EditText>(R.id.etMaxExpense)
        val btnSave = findViewById<Button>(R.id.btnSaveGoals)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        val database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            database.goalDao().getGoalByType("MIN_INCOME")?.let {
                etMinIncome.setText(it.targetAmount.toString())
            }
            database.goalDao().getGoalByType("MAX_EXPENSE")?.let {
                etMaxExpense.setText(it.targetAmount.toString())
            }
        }

        btnSave.setOnClickListener {
            val minIncome = etMinIncome.text.toString().toDoubleOrNull() ?: 0.0
            val maxExpense = etMaxExpense.text.toString().toDoubleOrNull() ?: 0.0

            lifecycleScope.launch {
                database.goalDao().insertGoal(Goal("MIN_INCOME", minIncome))
                database.goalDao().insertGoal(Goal("MAX_EXPENSE", maxExpense))
                Toast.makeText(this@SetGoalsActivity, "Goals Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
