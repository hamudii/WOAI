package com.bangkit.woai.views.authentication.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.woai.databinding.ActivityRegisterBinding
import com.bangkit.woai.views.authentication.login.LoginActivity

class RegisterActivity :  AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {
            val registerAct = Intent(this, LoginActivity::class.java)
            startActivity(registerAct)
        }
    }
}