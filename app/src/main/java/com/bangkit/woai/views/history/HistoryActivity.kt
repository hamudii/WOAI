package com.bangkit.woai.views.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.woai.R
import com.bangkit.woai.data.DummyData
import com.bangkit.woai.databinding.ActivityHistoryBinding
import com.bangkit.woai.views.main.HistoryTrainingAdapter

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapterHistory = HistoryTrainingAdapter(DummyData.historyTrainings, false)
        binding.rvHistoryAct.adapter = adapterHistory
        binding.rvHistoryAct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}