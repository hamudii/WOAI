package com.bangkit.woai.views.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.woai.data.DummyData
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.databinding.ActivityMainBinding
import com.bangkit.woai.views.history.HistoryActivity
import com.bangkit.woai.views.profile.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapterMain = WorkoutTrainingAdapter(DummyData.workoutTrainings, object : WorkoutTrainingAdapter.OnItemClickListener {
            override fun onItemClick(workoutTraining: WorkoutTraining) {
                showToast(workoutTraining.title)
            }
        })
        binding.rvMain.adapter = adapterMain
        binding.rvMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapterHistory = HistoryTrainingAdapter(DummyData.historyTrainings, true)
        binding.rvHistory.adapter = adapterHistory
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.txtViewMore.setOnClickListener {
            val historyAct = Intent(this, HistoryActivity::class.java)
            startActivity(historyAct)
        }

        binding.profileImage.setOnClickListener{
            val profileAct = Intent(this, ProfileActivity::class.java)
            startActivity(profileAct)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, "Clicked: $message", Toast.LENGTH_SHORT).show()
    }
}

