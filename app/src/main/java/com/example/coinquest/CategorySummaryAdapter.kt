package com.example.coinquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.CategoryTotal
import java.util.*

class CategorySummaryAdapter(private var totals: List<CategoryTotal>) :
    RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvAmount: TextView = view.findViewById(R.id.tvCategoryAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_total, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = totals[position]
        holder.tvName.text = item.category
        holder.tvAmount.text = String.format(Locale.getDefault(), "R%.2f", item.total)
    }

    override fun getItemCount() = totals.size

    fun updateData(newTotals: List<CategoryTotal>) {
        totals = newTotals
        notifyDataSetChanged()
    }
}
