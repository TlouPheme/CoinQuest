package com.example.coinquest

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinquest.data.AppDatabase
import com.example.coinquest.data.CategoryTotal
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var tvInsights: TextView
    
    private var startDate: Long = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.timeInMillis
    private var endDate: Long = System.currentTimeMillis()
    
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        barChart = findViewById(R.id.barChart)
        btnStartDate = findViewById(R.id.btnStartDate)
        btnEndDate = findViewById(R.id.btnEndDate)
        tvInsights = findViewById(R.id.tvInsights)

        updateDateButtons()

        btnStartDate.setOnClickListener { showDatePicker { date -> 
            startDate = date
            updateDateButtons()
            loadData()
        }}

        btnEndDate.setOnClickListener { showDatePicker { date -> 
            endDate = date
            updateDateButtons()
            loadData()
        }}

        loadData()
    }

    private fun updateDateButtons() {
        btnStartDate.text = "From: ${sdf.format(Date(startDate))}"
        btnEndDate.text = "To: ${sdf.format(Date(endDate))}"
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val selected = Calendar.getInstance()
            selected.set(year, month, day)
            onDateSelected(selected.timeInMillis)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadData() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val categories = db.transactionDao().getCategoryTotalsByPeriod(startDate, endDate, false).first()
            val goals = db.goalDao().getAllGoals().first()
            val maxExpenseGoal = goals.find { it.type == "MAX_EXPENSE" }?.targetAmount ?: 0.0
            
            updateChart(categories, maxExpenseGoal)
            generateInsights(categories, maxExpenseGoal)
        }
    }

    private fun updateChart(data: List<CategoryTotal>, goal: Double) {
        val entries = data.mapIndexed { index, item -> BarEntry(index.toFloat(), item.total.toFloat()) }
        val dataSet = BarDataSet(entries, "Spending by Category")
        dataSet.color = Color.parseColor("#FFD700")
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.category })
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.textColor = Color.WHITE
        
        // Add limit line for goal
        if (goal > 0) {
            val limitLine = com.github.mikephil.charting.components.LimitLine(goal.toFloat(), "Max Budget")
            limitLine.lineColor = Color.RED
            limitLine.lineWidth = 2f
            limitLine.textColor = Color.RED
            barChart.axisLeft.addLimitLine(limitLine)
        }

        barChart.invalidate()
    }

    private fun generateInsights(data: List<CategoryTotal>, goal: Double) {
        val totalSpending = data.sumOf { it.total }
        val highestCategory = data.maxByOrNull { it.total }
        
        val insightText = StringBuilder()
        insightText.append("Total Spending: R${"%.2f".format(totalSpending)}\n")
        
        if (highestCategory != null) {
            insightText.append("Highest spending in: ${highestCategory.category} (R${"%.2f".format(highestCategory.total)})\n")
        }
        
        if (goal > 0) {
            if (totalSpending > goal) {
                insightText.append("⚠️ You are R${"%.2f".format(totalSpending - goal)} over your budget!")
            } else {
                insightText.append("✅ You are staying within your budget. Good job!")
            }
        }
        
        tvInsights.text = insightText.toString()
    }
}
