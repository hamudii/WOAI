package com.bangkit.woai.views.authentication.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.preferences.UserPreference
import com.bangkit.woai.data.preferences.dataStore
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityLoginBinding
import com.bangkit.woai.views.authentication.register.RegisterActivity
import com.bangkit.woai.views.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceHelper(this)
        setupView()
        setupAction()

        val apiService = ApiConfig().getApiService(this)
        val userRepository = UserRepository.getInstance(apiService)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(userRepository)).get(LoginViewModel::class.java)

        viewModel.loginStatus.observe(this, { result ->
            binding.progressBar.visibility = View.GONE

            if (result != null && result.isSuccess) {
                showSuccessDialog()
            } else if (result != null && result.isFailure) {
                showErrorDialog(
                    result.exceptionOrNull()?.message ?: "Terjadi kesalahan saat login!"
                )
            }
        })
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginBtn.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            viewModel.login(email, password)
        }

        binding.registerBtn.setOnClickListener {
            val regisAct = Intent(this, RegisterActivity::class.java)
            startActivity(regisAct)
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Oops!")
            setMessage(message)
            setPositiveButton("Coba Lagi", null)
            create()
            show()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Selamat anda berhasil login")
            setPositiveButton("Lanjut") { _, _ ->

                val token = viewModel.getToken()
                val name = viewModel.getName()
                val id = viewModel.getID()
//                val id = sharedPref.getInt(Constant.PREF_ID)

                sharedPref.put(Constant.PREF_IS_LOGIN, true)
                sharedPref.put(Constant.PREF_EMAIL, binding.edLoginEmail.text.toString())
                sharedPref.put(Constant.PREF_NAME, name.toString())
                sharedPref.put(Constant.PREF_TOKEN, token.toString())
                sharedPref.put(Constant.PREF_ID, id.toString())

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

}
