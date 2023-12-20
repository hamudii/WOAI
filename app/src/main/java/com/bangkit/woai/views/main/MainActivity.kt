package com.bangkit.woai.views.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.woai.R
import com.bangkit.woai.data.DummyData
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityMainBinding
import com.bangkit.woai.views.authentication.register.RegisterViewModel
import com.bangkit.woai.views.authentication.register.RegisterViewModelFactory
import com.bangkit.woai.views.details_training.DetailTrainingActivity
import com.bangkit.woai.views.history.HistoryActivity
import com.bangkit.woai.views.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: PreferenceHelper
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        val apiService = ApiConfig().getApiService(this)
        val userRepository = UserRepository.getInstance(apiService)
        viewModel = ViewModelProvider(this, MainViewModelFactory(userRepository)).get(
            MainViewModel::class.java)

        sharedPref = PreferenceHelper(this)
        val prefName = sharedPref.getString(Constant.PREF_NAME)
        val avatarDrawables = arrayOf(
            R.drawable.avatar_emoji,
            R.drawable.avatar_emoji_2,
            R.drawable.avatar_emoji_3,
            R.drawable.avatar_emoji_4,
            R.drawable.avatar_emoji_5,
            R.drawable.avatar_emoji_6,
        )

        val random = Random()
        val randomIndex = random.nextInt(avatarDrawables.size)

        val selectedAvatar = avatarDrawables[randomIndex]
        binding.profileImage.setImageResource(selectedAvatar)
        sharedPref.putInt(Constant.PREF_IMG, selectedAvatar)

        val adapterMain = WorkoutTrainingAdapter(DummyData.workoutTrainings, object : WorkoutTrainingAdapter.OnItemClickListener {
            override fun onItemClick(workoutTraining: WorkoutTraining) {
                showToast(workoutTraining.title)

                // Pass data to DetailTrainingActivity
                val detailIntent = Intent(this@MainActivity, DetailTrainingActivity::class.java)
                detailIntent.putExtra("workoutTraining", workoutTraining)
                startActivity(detailIntent)
            }
        })
        binding.rvMain.adapter = adapterMain
        binding.rvMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapterHistory = HistoryTrainingAdapter(DummyData.historyTrainings, true)
        binding.rvHistory.adapter = adapterHistory
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.txtViewMore.setOnClickListener {
            val historyAct = Intent(this, HistoryActivity::class.java)
            startActivity(historyAct)
        }

        binding.profileImage.setOnClickListener{
            val profileAct = Intent(this, ProfileActivity::class.java)
            startActivity(profileAct)
        }

        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val statusTime = when (hourOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            else -> "Good Night"
        }
        binding.statusTime.text = statusTime

        binding.txtName.text = prefName

        val prefToken = sharedPref.getString(Constant.PREF_TOKEN) ?: ""
        val prefId = sharedPref.getString(Constant.PREF_ID)?.toIntOrNull() ?: 0

        viewModel.userActivityList.observe(this, Observer { userActivityList ->
            userActivityList?.let {
                Log.d("activity_user", "User Activity List: $it")
            }
        })

        // Observe the error message
        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Log.e("activity_user", "Error retrieving user data: $it")
//                Toast.makeText(applicationContext, "Error retrieving user data", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getUserActivity(prefId, prefToken)

        viewModel.historyTrainingList.observe(this, Observer { historyTrainingList ->
            historyTrainingList?.let {
                adapterHistory.updateData(it)
            }
        })

    }

    private fun showToast(message: String) {
//        Toast.makeText(this, "Clicked: $message", Toast.LENGTH_SHORT).show()
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