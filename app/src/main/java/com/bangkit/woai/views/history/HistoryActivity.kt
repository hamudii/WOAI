package com.bangkit.woai.views.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.woai.R
import com.bangkit.woai.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}