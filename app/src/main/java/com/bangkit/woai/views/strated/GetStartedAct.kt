package com.bangkit.woai.views.strated

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.woai.databinding.ActivityGetStartedBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bangkit.woai.R
import com.bangkit.woai.views.main.MainActivity
import eightbitlab.com.blurview.RenderScriptBlur

class GetStartedAct : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.drawable.get_started)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imgGetStarted)

        val radius = 10f
        binding.blurView.setupWith(binding.container)
            .setBlurRadius(radius)

        //intent to main act
        binding.btnStarted.setOnClickListener {
            val mainAct = Intent(this, MainActivity::class.java)
            startActivity(mainAct)
        }
    }
}
