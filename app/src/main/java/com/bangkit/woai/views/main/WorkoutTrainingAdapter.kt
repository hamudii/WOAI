package com.bangkit.woai.views.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.woai.data.DummyData.workoutTrainings
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.databinding.ItemCardMainBinding
import com.bumptech.glide.Glide

class WorkoutTrainingAdapter(private val workoutTraining: List<WorkoutTraining>) :
    RecyclerView.Adapter<WorkoutTrainingAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemCardMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workoutTraining: WorkoutTraining) {
            binding.textTitle.text = workoutTraining.title
            binding.imageView.setImageResource(workoutTraining.imageResId)

            Glide.with(binding.root)
                .load(workoutTraining.imageResId)
                .centerCrop()
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCardMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return workoutTrainings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(workoutTrainings[position])
    }
}