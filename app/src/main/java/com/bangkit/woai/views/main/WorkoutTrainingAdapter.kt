package com.bangkit.woai.views.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.databinding.ItemCardMainBinding
import com.bangkit.woai.views.details_training.DetailTrainingActivity
import com.bumptech.glide.Glide

class WorkoutTrainingAdapter(
    private val workoutTrainingList: List<WorkoutTraining>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<WorkoutTrainingAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(workoutTraining: WorkoutTraining)
    }

    class ViewHolder(private val binding: ItemCardMainBinding, private val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workoutTraining: WorkoutTraining) {
            binding.textTitle.text = workoutTraining.title
            Glide.with(binding.root)
                .load(workoutTraining.imageResId)
                .centerCrop()
                .into(binding.imageView)

            binding.root.setOnClickListener {
                listener.onItemClick(workoutTraining)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCardMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun getItemCount(): Int {
        return workoutTrainingList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(workoutTrainingList[position])
    }
}
