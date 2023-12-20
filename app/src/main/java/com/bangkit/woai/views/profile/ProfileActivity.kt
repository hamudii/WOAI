package com.bangkit.woai.views.profile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.protobuf.Api
import com.bangkit.woai.R
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityProfileBinding
import com.bangkit.woai.views.strated.GetStartedAct
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    lateinit var sharedPref: PreferenceHelper

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        val apiService = ApiConfig().getApiService(this)

        sharedPref = PreferenceHelper(this)
        val prefName = sharedPref.getString(Constant.PREF_NAME)
        val prefEmail = sharedPref.getString(Constant.PREF_EMAIL)
        val prefToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""
        val prefId = sharedPref.getString(Constant.PREF_ID)?.toIntOrNull() ?: 0
        val prefHeight = sharedPref.getString(Constant.PREF_HEIGHT)
        val prefWeight = sharedPref.getString(Constant.PREF_WEIGHT)
        val prefBmi = sharedPref.getString(Constant.PREF_BMI)
        val prefImg = sharedPref.getInt(Constant.PREF_IMG, R.drawable.avatar_emoji)
        Log.d("data pref", "data pref : $prefToken")

        binding.txtName.text = prefName
        binding.txtEmail.text = prefEmail
        binding.profileIvAvatar.setImageResource(prefImg)
        binding.btnBack.setOnClickListener {
            finish()
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val detailUserResponse = withContext(Dispatchers.IO) {
                    apiService.getUserData(prefId, prefToken)
                }
                detailUserResponse?.let {
                    val userData = it.data?.firstOrNull()
                    userData?.let {
                        val weight = userData.weight.toString()
                        val height = userData.height.toString()
                        val bmi = userData.bmi.toString()
                        val formattedBmi = String.format("%.2f", bmi.toDouble())
                        sharedPref.put(Constant.PREF_WEIGHT, weight)
                        sharedPref.put(Constant.PREF_HEIGHT, height)
                        sharedPref.put(Constant.PREF_BMI, formattedBmi)

                        if (prefHeight != null) {
                            binding.txtHeight.text = prefHeight
                        } else {
                            binding.txtHeight.text = height

                        }

                        if (prefWeight != null) {
                            binding.txtWeight.text = prefWeight
                        } else {
                            binding.txtWeight.text = weight

                        }

                        if (prefBmi != null) {
                            binding.txtBmi.text = prefBmi
                        } else {
                            binding.txtBmi.text = formattedBmi

                        }
                        binding.txtBmi.text = formattedBmi
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Error retrieving user data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        val tvLogoutMessage = dialogView.findViewById<TextView>(R.id.tvLogoutMessage)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirmLogout = dialogView.findViewById<Button>(R.id.btnConfirmLogout)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmLogout.setOnClickListener {
            // Handle logout action here
            performLogout()
            alertDialog.dismiss()
        }
    }

    private fun performLogout() {
        sharedPref.clear()
        val apiService = ApiConfig().getApiService(this)
        val prefToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val logoutResponse = withContext(Dispatchers.IO) {
                    apiService.logout(prefToken)
                }
                logoutResponse?.let {
                    Log.d("ProfileActivity", "Logout Status: ${it.loggedOutStatus}")
                    Log.d("ProfileActivity", "Logout Message: ${it.message}")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "Error retrieving user data",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        Toast.makeText(applicationContext, "Berhasil logout", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, GetStartedAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun hideSystemUI() {
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
}
