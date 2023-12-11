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
import com.bangkit.woai.views.authentication.login.LoginActivity
import com.bangkit.woai.views.main.MainActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import eightbitlab.com.blurview.RenderScriptBlur

class GetStartedAct : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding
    private lateinit var imageSlider: ImageSlider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.get_started))
        imageList.add(SlideModel(R.drawable.get_started_2))
        imageList.add(SlideModel(R.drawable.get_started_3))

        binding.imgSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)


        val radius = 10f
        binding.blurView.setupWith(binding.imgSlider)
            .setBlurRadius(radius)

        //intent to main act
        binding.btnStarted.setOnClickListener {
            val mainAct = Intent(this, LoginActivity::class.java)
            startActivity(mainAct)
        }
    }
}
