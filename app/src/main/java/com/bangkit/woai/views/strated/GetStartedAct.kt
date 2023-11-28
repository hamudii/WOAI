package com.bangkit.woai.views.strated


import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.woai.R
import com.bangkit.woai.databinding.ActivityGetStartedBinding
import com.bangkit.woai.views.BlurUtils

class GetStartedAct : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
