package com.bangkit.woai.views.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bangkit.woai.R
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.data.preferences.Constant
import com.bangkit.woai.data.preferences.PreferenceHelper
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.request.TrainingActivityRequest
import com.bangkit.woai.data.retrofit.ApiConfig
import com.bangkit.woai.databinding.ActivityNewCameraBinding
import com.bangkit.woai.ml.AutoModel15Desember2023
import com.bangkit.woai.views.details_training.DetailTrainingActivity
import com.bangkit.woai.views.history.HistoryActivity
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.ObjectDetector
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NewCameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewCameraBinding

    private lateinit var objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var tfliteModel: AutoModel15Desember2023
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var countDownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 0
    private var targetTimeMillis: Long = 0
    private var isRecording: Boolean = false
    private lateinit var viewModel: CameraViewModel
    private lateinit var sharedPref: PreferenceHelper
    private lateinit var cameraExecutor: ExecutorService
    private var toast: Toast? = null


    private var totalUpCorrect = 0
    private var totalDownCorrect = 0
    private var rightPushUpCount = 0
    private var upright = false
    private var downright = false

    private var lastTimestampMillis = 0L
    private var detectedFrameCount = 0
    private val framesPerSecondLimit = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCameraBinding.inflate(layoutInflater)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_camera)

        tfliteModel = AutoModel15Desember2023.newInstance(this)
        binding.rightPushupCount.text = "Total : $rightPushUpCount"
//        binding.txtResultUpCorrect.text = "Total : $totalUpCorrect"
//        binding.txtResultDownCorrect.text = "Total : $totalDownCorrect"

        sharedPref = PreferenceHelper(this)
        val apiService = ApiConfig().getApiService(this)
        val userRepository = UserRepository.getInstance(apiService)
        viewModel = ViewModelProvider(this, CameraViewModelFactory(userRepository)).get(
            CameraViewModel::class.java)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider, cameraSelector)
        }, ContextCompat.getMainExecutor(this))

        val localModel = LocalModel.Builder()
            .setAbsoluteFilePath("15Desember2023.tflite")
            .build()

        val radius = 5f
        binding.blurView.setupWith(binding.blurCardView)
            .setBlurRadius(radius)

        binding.btnSwitch.setOnClickListener {
            // Toggle between front and back cameras
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        binding.btnBack.setOnClickListener {
            countDownTimer?.cancel()
            finish()
            Toast.makeText(this, "Camera Closed", Toast.LENGTH_SHORT).show()
        }

        binding.btnPlayPause.setOnClickListener {
            toggleTimer()
        }

        hideSystemUI()

        val workoutTitle = intent.getStringExtra("workoutTitle")
        val workoutDuration = intent.getIntExtra("workoutDuration", 0)
        Log.d("NewCameraActivity", "Workout Title: $workoutTitle")
        Log.d("NewCameraActivity", "Workout Duration: $workoutDuration seconds")


    }

    private fun processFrame(bitmap: Bitmap) {
        val labels = assets.open("labels.txt").bufferedReader().use { it.readText() }.split("\n")
        val presentase = arrayListOf<String>()

        val tbuffer = resizePic(bitmap)
        val byteBuffer = tbuffer.buffer

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = tfliteModel.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        for (value in outputFeature0.floatArray) {
            val percentageString = convertToPercentage(value)
            presentase.add(percentageString)
        }

        val max = getMax(outputFeature0.floatArray)
        Log.d("max nilai", "$max")
        val predictedLabel = labels[max]

        Log.d("Akurasi", "Label: $predictedLabel, Presentasi: ${presentase.joinToString(", ")}")

        if (outputFeature0.floatArray[max] >= 0.99) {
            when (predictedLabel) {
                "2 Up-Correct" -> {
                    totalUpCorrect++
//                    updateCountTextView(binding.txtResultUpCorrect, "Total : $totalUpCorrect")
                    showToastOnUiThread("Hebat! Pertahankan kontrol saat naik, pastikan tubuh tetap lurus, dan otot inti terus bekerja.")
                    upright = true
                }

                "0 Down-Correct" -> {
                    totalDownCorrect++
//                    updateCountTextView(binding.txtResultDownCorrect, "Total : $totalDownCorrect")
                    showToastOnUiThread("Bagus! Pertahankan kontrol saat turun, pastikan tubuh tetap lurus, dan otot inti teraktivasi. ")
                    downright = true
                }

                "3 Up-Wrong" -> {
                    showToastOnUiThread("Pastikan tubuh Anda lurus dari kepala hingga tumit, tangan lebar dari bahu, jari-jari ke depan, siku membentuk sudut 45 derajat, dan aktifkan otot inti untuk stabilitas.")
                }

                "1 Down-Wrong" -> {
                    showToastOnUiThread("Pastikan turun dengan kontrol penuh, tubuh tetap lurus, tangan lebar dari bahu, dan otot inti aktif.")
                }

                else -> {
                    Log.d("null_total", "total 0")
                }
            }

            if (upright && downright) {
                rightPushUpCount++
                updateCountTextView(binding.rightPushupCount, "Total : $rightPushUpCount")
                upright = false
                downright = false
            }
        }

        showToast(labels[max])
    }

    private fun updateCountTextView(textView: TextView, text: String) {
        runOnUiThread {
            textView.text = text
        }
    }

    private fun convertToPercentage(value: Float): String {
        val percentage = (value * 100).toInt()
        return "$percentage%"
    }

    private fun showToast(message: String) {
        runOnUiThread {
//            binding.result.text = message
        }
    }

    private fun showToastOnUiThread(message: String) {
        try {
            runOnUiThread {
                if (!isFinishing) {
                    toast?.cancel() // Hentikan Toast sebelumnya jika ada
                    toast = Toast.makeText(this@NewCameraActivity, message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
        } catch (e: Exception) {
            Log.e("NewCameraActivity", "Error showing toast: ${e.message}")
        }
    }

    private fun resizePic(image: Bitmap): TensorImage {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(1f, 255f))
            .build()
        var tImage = TensorImage(DataType.FLOAT32)
        tImage.load(image)
        tImage = imageProcessor.process(tImage)
        return tImage
    }

    private fun getMax(arr: FloatArray): Int {
        var ind = 0
        var max = arr[0]

        for (i in arr.indices) {
            Log.d("accuracy", max.toString())
            if (arr[i] > max) {
                max = arr[i]
                ind = i
            }
        }
        return ind
    }

    override fun onResume() {
        super.onResume()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onPause() {
        super.onPause()
        cameraExecutor.shutdown()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider, cameraSelector: CameraSelector) {
        val preview = Preview.Builder().build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor, { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = imageProxy.image

            if (image != null) {
                val currentTimestampMillis = System.currentTimeMillis()

                if (currentTimestampMillis - lastTimestampMillis >= 200) {
                    lastTimestampMillis = currentTimestampMillis

                    val bitmap = imageProxy.toBitmap()
                    processFrame(bitmap)

                    detectedFrameCount++
                    if (detectedFrameCount >= framesPerSecondLimit) {
                        detectedFrameCount = 0
                        lastTimestampMillis = currentTimestampMillis
                    }
                }

                imageProxy.close()
            }
        })
        showLogoutConfirmationDialog()
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }

    private fun startCamera() {
        closeCamera()
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider, cameraSelector)
            showLogoutConfirmationDialog()
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        toast?.cancel()
        finish()


    }

    private fun closeCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun updateTimerText(elapsedMillis: Long) {
        val seconds = elapsedMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val timerText = String.format("%02d:%02d", minutes, remainingSeconds)
        binding.txtTimer.text = timerText
    }

    private fun toggleTimer() {
        val prefId = sharedPref.getString(Constant.PREF_ID)?.toIntOrNull() ?: 0
        val prefToken = sharedPref.getString(Constant.PREF_TOKEN)?:""
        val workoutTitle = intent.getStringExtra("workoutTitle")
        val workoutDuration = intent.getIntExtra("workoutDuration", 0)
        if (countDownTimer == null) {
            // Tambahkan hitungan mundur 3 detik
            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    binding.txtCountdown.text = secondsRemaining.toString()

                    binding.txtCountdown.visibility = if (secondsRemaining > 0) View.VISIBLE else View.GONE

                }

                override fun onFinish() {

                    val targetTimeMillis = workoutDuration * 1000L

                    countDownTimer = object : CountDownTimer(
                        if (remainingTimeMillis > 0) remainingTimeMillis else targetTimeMillis,
                        1000
                    ) {
                        override fun onTick(millisUntilFinished: Long) {
                            remainingTimeMillis = millisUntilFinished
                            updateTimerText(millisUntilFinished)
                        }

                        override fun onFinish() {
                            finish()
                            val trainingActivityRequest = TrainingActivityRequest(
                                duration = (targetTimeMillis / 1000).toInt(),
                                total = rightPushUpCount,
                                correct = null,
                                incorrect = null,
                                userId = prefId,
                                upCorrect = null,
                                description = null,
                                downCorrect = null,
                                type = workoutTitle
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
