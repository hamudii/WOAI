package com.bangkit.woai.views.authentication.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.woai.data.preferences.UserPreference
import com.bangkit.woai.data.preferences.dataStore
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityRegisterBinding
import com.bangkit.woai.di.Injection
import com.bangkit.woai.views.authentication.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        val apiService = ApiConfig().getApiService(this)
        val userRepository = UserRepository.getInstance(apiService)
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(userRepository)).get(RegisterViewModel::class.java)

        viewModel.registrationStatus.observe(this, { result ->
            if (result.isSuccess) {
                val response = result.getOrNull()
                if (response != null && response.message == "user successfully created") {
                    showSuccessDialog()
                } else {
                    showErrorDialog("Terjadi kesalahan saat mendaftar!")
                }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Terjadi kesalahan saat mendaftar!"
                showErrorDialog(errorMessage)
            }
        })

        viewModel.isLoading.observe(this, { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this, { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showErrorDialog(errorMessage)
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
        binding.registerBtn.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val weight = binding.edRegisterWeight.text.toString().toInt()
            val height = binding.edRegisterHeight.text.toString().toInt()
            val password = binding.edRegisterPassword.text.toString()
            val checkBox = binding.checkBoxAgree.isChecked

            if (checkBox) {
                viewModel.register(name, email, weight, height, password)
            } else {
                showErrorDialog("Harap centang kotak persetujuan untuk melanjutkan registrasi.")
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Selamat akun dengan ${binding.edRegisterName.text.toString()} sudah berhasil dibuat!")
            setPositiveButton("Lanjut") { _, _ ->
                finish()
            }
            create()
            show()
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
}