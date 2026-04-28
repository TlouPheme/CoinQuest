package com.example.coinquest

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.AppDatabase
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Displays an aggregated view of transactions grouped by category within a selected date range.
 * Separates Income and Expenses into two distinct lists for clarity.
 */
class CategorySummaryActivity : AppCompatActivity() {

    private lateinit var expenseAdapter: CategorySummaryAdapter
    private lateinit var incomeAdapter: CategorySummaryAdapter
    
    // Time range state
    private var startDate: Long = 0
    private var endDate: Long = System.currentTimeMillis()
    
    // Coroutine handles to manage multiple data streams efficiently
    private var expenseJob: Job? = null
    private var incomeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_summary)

        // Retrieve optional date range from intent (passed from History screen)
        startDate = intent.getLongExtra("START_DATE", 0L)
        endDate = intent.getLongExtra("END_DATE", System.currentTimeMillis())

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnFilter = findViewById<View>(R.id.btnFilterPeriod)
        val tvPeriod = findViewById<TextView>(R.id.tvPeriod)
        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpensesSummary)
        val rvIncome = findViewById<RecyclerView>(R.id.rvIncomeSummary)
        // (Gemini, 2026)

        btnBack.setOnClickListener { finish() }

        // Setup Expense RecyclerView
        rvExpenses.layoutManager = LinearLayoutManager(this)
        expenseAdapter = CategorySummaryAdapter(emptyList())
        rvExpenses.adapter = expenseAdapter
        // (Gemini, 2026)

        // Setup Income RecyclerView
        rvIncome.layoutManager = LinearLayoutManager(this)
        incomeAdapter = CategorySummaryAdapter(emptyList())
        rvIncome.adapter = incomeAdapter
        // (Gemini, 2026)

        val database = AppDatabase.getDatabase(this)

        /**
         * Date Range Selection:
         * Uses Material Design Date Range Picker to allow users to filter their financial report.
         */
        btnFilter.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Period")
                .setSelection(Pair(startDate, endDate))
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener { selection ->
                startDate = selection.first
                /** 
                 * Adjustment: Date picker returns the start of the day in UTC.
                 * Adding 23:59:59 hours to include transactions on the final day.
                 */
                endDate = selection.second + (24 * 60 * 60 * 1000 - 1)
                updatePeriodText(tvPeriod)
                loadSummaries(database)
            }
        }

        // Default initialization logic if no dates were provided
        if (startDate == 0L) {
            // Default to the last 30 days
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            endDate = calendar.timeInMillis
            
            calendar.add(Calendar.DAY_OF_YEAR, -30)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            startDate = calendar.timeInMillis
        }
        
        updatePeriodText(tvPeriod)
        loadSummaries(database)
    }

    /** Updates the displayed date range label. */
    private fun updatePeriodText(tvPeriod: TextView) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvPeriod.text = String.format(Locale.getDefault(), "%s - %s", dateFormat.format(Date(startDate)), dateFormat.format(Date(endDate)))
    }

    /**
     * Data Loading Logic:
     * Observes categorized totals from the database and updates the adapters reactively.
     * Previous jobs are canceled to ensure only one listener is active per data type.
     */
    private fun loadSummaries(database: AppDatabase) {
        expenseJob?.cancel()
        incomeJob?.cancel()

        // Observe Expenses
        expenseJob = lifecycleScope.launch {
            database.transactionDao().getCategoryTotalsByPeriod(startDate, endDate, false).collect { totals ->
                expenseAdapter.updateData(totals)
            }
        }
        // Observe Income
        incomeJob = lifecycleScope.launch {
            database.transactionDao().getCategoryTotalsByPeriod(startDate, endDate, true).collect { totals ->
                incomeAdapter.updateData(totals)
            }
        }
    }
}
