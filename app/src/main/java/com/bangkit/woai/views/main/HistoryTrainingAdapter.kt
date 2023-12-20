package com.bangkit.woai.views.main

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.woai.data.HistoryTraining
import com.bangkit.woai.databinding.ItemCardHistoryBinding
import com.bangkit.woai.views.training_summary.TrainingSummaryActivity

class HistoryTrainingAdapter(
    private var historyTrainings: List<HistoryTraining>,
    private val isMainActivity: Boolean = true
) :
    RecyclerView.Adapter<HistoryTrainingAdapter.HistoryTrainingViewHolder>() {

    private val maxItemCount = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryTrainingViewHolder {
        val binding = ItemCardHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryTrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryTrainingViewHolder, position: Int) {
        val historyTraining = historyTrainings[position]
        holder.bind(historyTraining)
    }

    override fun getItemCount(): Int {
        return if (isMainActivity) minOf(historyTrainings.size, maxItemCount)
        else historyTrainings.size
    }

    class HistoryTrainingViewHolder(private val binding: ItemCardHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyTraining: HistoryTraining) {
            binding.txtDate.text = historyTraining.date
            binding.txtTime.text = historyTraining.time

            itemView.setOnClickListener {
                Log.d("HistoryTrainingAdapter", "Item clicked - Date: ${historyTraining.date}, Time: ${historyTraining.time}, id: ${historyTraining.id}")

                val intent = Intent(itemView.context, TrainingSummaryActivity::class.java)
                intent.putExtra("historyTrainingId", historyTraining.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    fun updateData(newData: List<HistoryTraining>) {
        historyTrainings = newData
        notifyDataSetChanged()
    }
}