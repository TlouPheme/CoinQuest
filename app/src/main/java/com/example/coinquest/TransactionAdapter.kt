package com.example.coinquest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for the RecyclerView in [TransactionListActivity].
 * Manages the display of individual transaction items, including color coding for Income vs Expenses.
 */
class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    // (Gemini, 2026)

    /**
     * ViewHolder holds references to the UI elements for a single list item.
     */
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // (Gemini, 2026)
        val ivTypeIcon: ImageView = view.findViewById(R.id.ivTypeIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCategoryDate: TextView = view.findViewById(R.id.tvCategoryDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    /**
     * Binds data from a [Transaction] object to the UI elements.
     * Applies conditional styling based on whether the transaction is an income or an expense.
     */
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvTitle.text = transaction.title
        
        // Format the timestamp into a readable date string
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = dateFormat.format(Date(transaction.date))
        holder.tvCategoryDate.text = String.format(Locale.getDefault(), "%s • %s", transaction.category, dateString)
        
        // Apply styling based on transaction type
        if (transaction.isIncome) {
            holder.ivTypeIcon.setImageResource(R.drawable.ic_income)
            holder.ivTypeIcon.setColorFilter(Color.parseColor("#4CAF50")) // Green for income
            holder.tvAmount.text = String.format(Locale.getDefault(), "+R%.2f", transaction.amount)
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.ivTypeIcon.setImageResource(R.drawable.ic_expense)
            holder.ivTypeIcon.setColorFilter(Color.parseColor("#EF5350")) // Red for expense
            holder.tvAmount.text = String.format(Locale.getDefault(), "-R%.2f", transaction.amount)
            holder.tvAmount.setTextColor(Color.parseColor("#EF5350"))
        }
    }

    override fun getItemCount() = transactions.size

    /**
     * Updates the data set and refreshes the list.
     */
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
