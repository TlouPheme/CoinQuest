package com.example.coinquest

import android.content.Intent
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
 * Detailed transaction history view.
 * Allows users to scroll through individual transactions and filter them by date range.
 * Users can also jump to the Category Summary from here with their current filter applied.
 */
class TransactionListActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionAdapter
    private var startDate: Long = 0
    private var endDate: Long = System.currentTimeMillis()
    private var collectionJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        val rvTransactions = findViewById<RecyclerView>(R.id.rvTransactions)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnFilter = findViewById<View>(R.id.btnFilterPeriod)
        val tvPeriod = findViewById<TextView>(R.id.tvPeriod)
        val btnSummary = findViewById<ImageButton>(R.id.btnSummary)

        // Setup the list adapter
        rvTransactions.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(emptyList())
        rvTransactions.adapter = adapter

        btnBack.setOnClickListener { finish() }

        /**
         * Navigation to Summary:
         * Passes the currently selected date range to CategorySummaryActivity 
         * so the user doesn't have to select the period again.
         */
        btnSummary.setOnClickListener {
            val intent = Intent(this, CategorySummaryActivity::class.java)
            intent.putExtra("START_DATE", startDate)
            intent.putExtra("END_DATE", endDate)
            startActivity(intent)
        }

        val database = AppDatabase.getDatabase(this)

        /**
         * Filter Period:
         * Triggers a Material Date Range Picker.
         */
        btnFilter.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Period")
                .setSelection(Pair(startDate, endDate))
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener { selection ->
                startDate = selection.first
                // Adjust to end of day to include all transactions on the last day
                endDate = selection.second + (24 * 60 * 60 * 1000 - 1)
                updatePeriodText(tvPeriod)
                loadTransactions(database)
            }
        }

        // Initialize with default date range (Last 30 days)
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
        
        updatePeriodText(tvPeriod)
        loadTransactions(database)
    }

    /** Updates the period UI label. */
    private fun updatePeriodText(tvPeriod: TextView) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvPeriod.text = String.format(Locale.getDefault(), "%s - %s", dateFormat.format(Date(startDate)), dateFormat.format(Date(endDate)))
    }

    /**
     * Data Observation:
     * Starts a reactive flow to fetch transactions from the database for the given range.
     * Cancels any existing observation job to prevent multiple listeners.
     */
    private fun loadTransactions(database: AppDatabase) {
        collectionJob?.cancel()
        collectionJob = lifecycleScope.launch {
            database.transactionDao().getTransactionsByPeriod(startDate, endDate).collect { transactions ->
                adapter.updateData(transactions)
            }
        }
    }
}
