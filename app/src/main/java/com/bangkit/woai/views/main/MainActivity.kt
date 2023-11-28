package com.bangkit.woai.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.woai.R
import com.bangkit.woai.data.DummyData
import com.bangkit.woai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = WorkoutTrainingAdapter(DummyData.workoutTrainings)
        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapterHistory = HistoryTrainingAdapter(DummyData.historyTrainings)
        binding.rvHistory.adapter = adapterHistory
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}