package com.bangkit.woai.views.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.woai.R
import com.bangkit.woai.data.HistoryTraining
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.databinding.ItemCardHistoryBinding
import com.bangkit.woai.databinding.ItemCardMainBinding
import com.bumptech.glide.Glide

class HistoryTrainingAdapter(private val historyTrainings: List<HistoryTraining>, private val isMainActivity: Boolean = true) :
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
        }
    }
}