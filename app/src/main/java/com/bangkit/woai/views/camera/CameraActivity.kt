package com.bangkit.woai.views.camera

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bangkit.woai.R
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.request.TrainingActivityRequest
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var countDownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 0
    private var isRecording: Boolean = false
    private lateinit var viewModel: CameraViewModel
    private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val radius = 5f
        binding.blurView.setupWith(binding.blurCardView)
            .setBlurRadius(radius)

        binding.btnSwitch.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        binding.btnPlayPause.setOnClickListener {
            toggleTimer()
        }

        binding.btnBack.setOnClickListener {
            countDownTimer?.cancel()
            closeCamera()
        }

        sharedPref = PreferenceHelper(this)
        val prefToken  = sharedPref.getString(Constant.PREF_TOKEN) ?: ""
        val prefId = sharedPref.getString(Constant.PREF_ID)?.toIntOrNull() ?: 0

        val apiService = ApiConfig().getApiService(this)
        val userRepository = UserRepository.getInstance(apiService)
        viewModel = ViewModelProvider(this, CameraViewModelFactory(userRepository)).get(
            CameraViewModel::class.java)

    }


    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_guide, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        val btnGotIt = dialogView.findViewById<Button>(R.id.btnGotIt)

        btnGotIt.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                showLogoutConfirmationDialog()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
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

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    private fun toggleTimer() {
        val prefId = sharedPref.getString(Constant.PREF_ID)?.toIntOrNull() ?: 0
        val prefToken = sharedPref.getString(Constant.PREF_TOKEN)?:""
        if (countDownTimer == null) {
            // Tambahkan hitungan mundur 3 detik
            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Update UI atau lakukan hal lain selama hitungan mundur
                    val secondsRemaining = millisUntilFinished / 1000
                    // Misalnya, tampilkan hitungan mundur pada suatu tempat di UI
                    binding.txtCountdown.text = secondsRemaining.toString()

                    binding.txtCountdown.visibility = if (secondsRemaining > 0) View.VISIBLE else View.GONE

                }

                override fun onFinish() {
                    // Setelah hitungan mundur 3 detik selesai, mulai timer utama
                    val targetTimeMillis = 10000L

                    countDownTimer = object : CountDownTimer(
                        if (remainingTimeMillis > 0) remainingTimeMillis else targetTimeMillis,
                        1000
                    ) {
                        override fun onTick(millisUntilFinished: Long) {
                            remainingTimeMillis = millisUntilFinished
                            updateTimerText(millisUntilFinished)
                        }

                        override fun onFinish() {
                            // Timer finished, close the camera or perform any other action
                            closeCamera()
                            val trainingActivityRequest = TrainingActivityRequest(
                                duration = 60,
                                total = 8,
                                correct = null,
                                incorrect = null,
                                userId = prefId,
                                upCorrect = null,
                                description = "kelazz test",
                                downCorrect = null,
                                type = "60-Second-PushUp"
                            )

                            viewModel.addUserActivity(prefToken, trainingActivityRequest)
                        }
                    }.start()
                    binding.btnPlayPause.setIconResource(R.drawable.baseline_pause_24)
                }
            }.start()
        } else {
            countDownTimer?.cancel()
            countDownTimer = null
            binding.btnPlayPause.setIconResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun closeCamera() {
        // Close the camera logic here
        Toast.makeText(this@CameraActivity, "Camera closed", Toast.LENGTH_SHORT).show()
        // Add your logic to close the camera or perform any other actions
        // For example, you can call finish() to close the activity
        finish()
    }

    private fun updateTimerText(elapsedMillis: Long) {
        val seconds = elapsedMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val timerText = String.format("%02d:%02d", minutes, remainingSeconds)
        binding.txtTimer.text = timerText
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}