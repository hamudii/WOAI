package com.bangkit.woai.views.history

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.woai.R
import com.bangkit.woai.data.DummyData
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityHistoryBinding
import com.bangkit.woai.views.main.HistoryTrainingAdapter
import com.bangkit.woai.views.main.MainViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var sharedPref: PreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        sharedPref = PreferenceHelper(this)
        val apiService = ApiConfig().getApiService(this)
        val userRepository = UserRepository.getInstance(apiService)
        viewModel = ViewModelProvider(this, HistoryModelFactory(userRepository))
            .get(HistoryViewModel::class.java)

        val adapterHistory = HistoryTrainingAdapter(emptyList(), false)
        binding.rvHistoryAct.adapter = adapterHistory
        binding.rvHistoryAct.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.userActivityList.observe(this, Observer { userActivityList ->
            userActivityList?.let {
                Log.d("activity_user", "User Activity List: $it")
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Log.e("activity_user", "Error retrieving user data: $it")
                Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        val prefToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""
        val prefId = sharedPref.getString(Constant.PREF_ID)?.toIntOrNull() ?: 0
        viewModel.getUserActivity(prefId, prefToken)

        viewModel.historyTrainingList.observe(this, Observer { historyTrainingList ->
            historyTrainingList?.let {
                adapterHistory.updateData(it)
            }
        })

        viewModel.emptyState.observe(this, Observer { isEmpty ->
            if (isEmpty) {
                binding.animationView.visibility = View.VISIBLE
                binding.rvHistoryAct.visibility = View.GONE
            } else {
                binding.animationView.visibility = View.GONE
                binding.rvHistoryAct.visibility = View.VISIBLE
            }
        })

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