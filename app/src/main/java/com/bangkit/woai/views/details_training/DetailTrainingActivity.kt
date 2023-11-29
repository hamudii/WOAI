package com.bangkit.woai.views.details_training

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.woai.R
import com.bangkit.woai.databinding.ActivityDetailTrainingBinding

class DetailTrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTrainingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val radius = 5f
        binding.blurView.setupWith(binding.container)
            .setBlurRadius(radius)

        binding.btnStart.setOnClickListener{

        }

    }
}