package com.bangkit.woai.views.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.woai.R
import com.bangkit.woai.views.strated.GetStartedAct

class SplashActivity : AppCompatActivity() {
    private val splashDelay: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, GetStartedAct::class.java)
            startActivity(intent)
            finish()
        }, splashDelay)
    }
}