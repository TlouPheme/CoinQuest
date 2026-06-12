package com.example.coinquest

object FinanceUtils {
    /**
     * Logic to determine financial health based on savings ratio.
     * @return Percentage of income saved, clamped between 0 and 100.
     */
    fun calculateFinancialHealth(income: Double, expenses: Double): Int {
        if (income <= 0.0) return 0
        if (expenses >= income) return 0
        val savings = income - expenses
        return ((savings / income) * 100).toInt().coerceIn(0, 100)
    }
}
