package com.example.coinquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinquest.data.AppDatabase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * The main hub of the application.
 * Displays financial summaries, goal tracking, and provides navigation to all features.
 */
class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // UI element initialization
        val tvIncome = findViewById<TextView>(R.id.tvIncome)
        val tvExpenses = findViewById<TextView>(R.id.tvExpenses)
        val tvIncomeGoal = findViewById<TextView>(R.id.tvIncomeGoalStatus)
        val tvExpenseGoal = findViewById<TextView>(R.id.tvExpenseGoalStatus)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val btnAdd = findViewById<Button>(R.id.btnAddTransaction)
        val btnSetGoals = findViewById<Button>(R.id.btnSetGoals)
        val btnViewHistory = findViewById<Button>(R.id.btnViewHistory)
        val btnCategorySummary = findViewById<Button>(R.id.btnCategorySummary)

        val database = AppDatabase.getDatabase(this)

        // Navigation setup
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        btnSetGoals.setOnClickListener {
            startActivity(Intent(this, SetGoalsActivity::class.java))
        }

        btnViewHistory.setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }

        btnCategorySummary.setOnClickListener {
            startActivity(Intent(this, CategorySummaryActivity::class.java))
        }

        /**
         * Real-time Data Observation:
         * Combines multiple database flows (Total Income, Total Expenses, and Goals) 
         * to update the UI whenever any of these values change.
         */
        lifecycleScope.launch {
            combine(
                database.transactionDao().getTotalIncome(),
                database.transactionDao().getTotalExpenses(),
                database.goalDao().getAllGoals()
            ) { income, expenses, goals ->
                Triple(income ?: 0.0, expenses ?: 0.0, goals)
            }.collect { (income, expenses, goals) ->
                // Update basic totals
                tvIncome.text = getString(R.string.total_income_r0_00).replace("R0.00", "R%.2f".format(income))
                tvExpenses.text = getString(R.string.total_expenses_r0_00).replace("R0.00", "R%.2f".format(expenses))

                // Find specific goals
                val minIncomeGoal = goals.find { it.type == "MIN_INCOME" }?.targetAmount ?: 0.0
                val maxExpenseGoal = goals.find { it.type == "MAX_EXPENSE" }?.targetAmount ?: 0.0

                // Update goal status text
                tvIncomeGoal.text = getString(R.string.income_goal, income, minIncomeGoal)
                tvExpenseGoal.text = getString(R.string.expense_limit, expenses, maxExpenseGoal)
                
                // Visual feedback: Color coding for goal status
                if (maxExpenseGoal > 0 && expenses > maxExpenseGoal) {
                    tvExpenseGoal.setTextColor(android.graphics.Color.RED) // Over budget
                } else {
                    tvExpenseGoal.setTextColor(android.graphics.Color.WHITE)
                }

                if (minIncomeGoal > 0 && income >= minIncomeGoal) {
                    tvIncomeGoal.setTextColor(android.graphics.Color.GREEN) // Goal met
                } else {
                    tvIncomeGoal.setTextColor(android.graphics.Color.WHITE)
                }
                
                // Calculate and display financial health percentage
                val score = calculateFinancialHealth(income, expenses)
                tvScore.text = "$score%"
            }
        }
    }

    /**
     * Logic to determine financial health based on savings ratio.
     * @return Percentage of income saved, clamped between 0 and 100.
     */
    private fun calculateFinancialHealth(income: Double, expenses: Double): Int {
        if (income == 0.0) return 0
        val savings = income - expenses
        return ((savings / income) * 100).toInt().coerceIn(0, 100)
    }
}
