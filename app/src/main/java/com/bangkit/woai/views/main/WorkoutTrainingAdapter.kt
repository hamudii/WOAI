package com.bangkit.woai.views.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.databinding.ItemCardMainBinding
import com.bangkit.woai.views.details_training.DetailTrainingActivity
import com.bumptech.glide.Glide

class WorkoutTrainingAdapter(
    private val workoutTraining: List<WorkoutTraining>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<WorkoutTrainingAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(workoutTraining: WorkoutTraining)
    }

    class ViewHolder(private val binding: ItemCardMainBinding, private val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workoutTraining: WorkoutTraining) {
            binding.textTitle.text = workoutTraining.title
            binding.imageView.setImageResource(workoutTraining.imageResId)

            Glide.with(binding.root)
                .load(workoutTraining.imageResId)
                .centerCrop()
                .into(binding.imageView)

            binding.root.setOnClickListener {
                listener.onItemClick(workoutTraining)
                val detailAct = Intent(binding.root.context, DetailTrainingActivity::class.java)
                startActivity(binding.root.context, detailAct, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCardMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun getItemCount(): Int {
        return workoutTraining.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(workoutTraining[position])
    }
}
