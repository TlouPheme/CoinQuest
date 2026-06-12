package com.example.coinquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.SavingsGoal

class SavingsGoalAdapter(private val goals: List<SavingsGoal>) : RecyclerView.Adapter<SavingsGoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvGoalName)
        val status: TextView = view.findViewById(R.id.tvSavingsStatus)
        val progress: ProgressBar = view.findViewById(R.id.pbSavings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_savings_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.name.text = goal.name
        holder.status.text = "R${"%.2f".format(goal.currentAmount)} / R${"%.2f".format(goal.targetAmount)}"
        holder.progress.max = goal.targetAmount.toInt()
        holder.progress.progress = goal.currentAmount.toInt()
    }

    override fun getItemCount() = goals.size
}
