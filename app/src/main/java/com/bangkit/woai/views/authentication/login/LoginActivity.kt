package com.bangkit.woai.views.authentication.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.woai.databinding.ActivityLoginBinding
import com.bangkit.woai.views.authentication.register.RegisterActivity
import com.bangkit.woai.views.main.MainActivity

class LoginActivity :  AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val loginAct = Intent(this, MainActivity::class.java)
            startActivity(loginAct)
        }

        binding.registerBtn.setOnClickListener {
            val registerAct = Intent(this, RegisterActivity::class.java)
            startActivity(registerAct)
        }
    }
}