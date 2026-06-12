package com.example.coinquest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.AppDatabase
import com.example.coinquest.data.SavingsGoal
import kotlinx.coroutines.launch

class SavingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings)

        val etName = findViewById<EditText>(R.id.etGoalName)
        val etTarget = findViewById<EditText>(R.id.etTargetAmount)
        val btnAdd = findViewById<Button>(R.id.btnAddGoal)
        val rvGoals = findViewById<RecyclerView>(R.id.rvSavingsGoals)

        val db = AppDatabase.getDatabase(this)
        
        rvGoals.layoutManager = LinearLayoutManager(this)

        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val target = etTarget.text.toString().toDoubleOrNull() ?: 0.0
            
            if (name.isNotEmpty() && target > 0) {
                lifecycleScope.launch {
                    db.savingsGoalDao().insert(SavingsGoal(name = name, targetAmount = target))
                    etName.text.clear()
                    etTarget.text.clear()
                    Toast.makeText(this@SavingsActivity, "Goal Added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            db.savingsGoalDao().getAllSavingsGoals().collect { goals ->
                rvGoals.adapter = SavingsGoalAdapter(goals)
            }
        }
    }
}
