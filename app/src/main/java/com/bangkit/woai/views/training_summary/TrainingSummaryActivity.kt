package com.bangkit.woai.views.training_summary

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.response.SpecificActivityResponse
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityTrainningSummaryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrainingSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainningSummaryBinding
    private lateinit var viewModel: TrainingSummaryViewModel
    private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainningSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        sharedPref = PreferenceHelper(this)

        val historyTrainingId = intent.getIntExtra("historyTrainingId", 0)
        Log.d("historyId", "$historyTrainingId")

        val userRepository = UserRepository.getInstance(ApiConfig().getApiService(this))
        viewModel = ViewModelProvider(this, TrainingSummaryViewModelFactory(userRepository))
            .get(TrainingSummaryViewModel::class.java)

        val prefToken = sharedPref.getString(Constant.PREF_TOKEN)
        val id = historyTrainingId

        if (prefToken != null) {
            viewModel.getSpecificActivity(id, prefToken)
        }
        viewModel.specificActivity.observe(this, Observer { specificActivityResponse ->
            Log.d("TrainingSummaryActivity", "Specific Activity Response: $specificActivityResponse")
            updateUI(specificActivityResponse)
        })

        val videoItem = MediaItem.fromUri("https://raw.githubusercontent.com/rahoelr/WOAI_NEW_FIX/main/pushup_tutorial.mp4\n")
        val player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            exoPlayer.setMediaItem(videoItem)
            exoPlayer.prepare()
        }
        binding.workoutVideoView.player = player
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

    private fun updateUI(specificActivityResponse: SpecificActivityResponse) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val createdAtDate = dateFormat.parse(specificActivityResponse.data?.get(0)?.createdAt ?: "")
        val formattedDate = dateFormat.format(createdAtDate ?: "")
        binding.txtDate.text = formattedDate
        binding.txtTraining.text = specificActivityResponse.data?.get(0)?.type ?: ""
        binding.txtReps.text = specificActivityResponse.data?.get(0)?.total?.toString() + " reps"
        binding.txtTime.text = specificActivityResponse.data?.get(0)?.duration?.let { formatTime(it) } ?: ""
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

}