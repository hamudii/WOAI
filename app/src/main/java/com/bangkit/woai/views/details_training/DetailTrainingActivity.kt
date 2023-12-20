package com.bangkit.woai.views.details_training

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bangkit.woai.R
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.databinding.ActivityDetailTrainingBinding
import com.bangkit.woai.views.camera.CameraActivity
import com.bangkit.woai.views.camera.NewCameraActivity

class DetailTrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTrainingBinding
    private var workoutTraining: WorkoutTraining? = null
    private var workoutTitle: String? = null
    private var workoutDuration: Int = 0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val radius = 5f
        binding.blurView.setupWith(binding.container)
            .setBlurRadius(radius)

        binding.btnStart.setOnClickListener {
            if (!allPermissionsGranted()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            } else {
//                openCamera()
                openNewCamera()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        hideSystemUI()

        val workoutTraining = intent.getSerializableExtra("workoutTraining") as? WorkoutTraining

        if (workoutTraining != null) {
            // Log the details
            Log.d("DetailTrainingActivity", "Received Workout Training:")
            Log.d("DetailTrainingActivity", "ID: ${workoutTraining.id}")
            Log.d("DetailTrainingActivity", "Title: ${workoutTraining.title}")
            Log.d("DetailTrainingActivity", "Image Resource ID: ${workoutTraining.imageResId}")
            Log.d("DetailTrainingActivity", "Description: ${workoutTraining.description}")
            Log.d("DetailTrainingActivity", "Duration: ${workoutTraining.duration}")
            Log.d("DetailTrainingActivity", "Kcal: ${workoutTraining.kcal}")

            val durationInMinutes = workoutTraining.duration / 60
            binding.imgDetailTraining.setImageResource(workoutTraining.imageResId)
            binding.txtTitle.text = workoutTraining.title
            binding.txtBurnKcal.text = "${workoutTraining.kcal} Kcal"
            binding.txtTimeMin.text = "${durationInMinutes} min"
            binding.txtDescription.text = workoutTraining.description

            workoutTitle = workoutTraining.title
            workoutDuration = workoutTraining.duration

            // Use workoutTraining as needed
        } else {
            Log.e("DetailTrainingActivity", "Failed to retrieve Workout Training from intent.")
        }
    }

    private fun openCamera() {
        val intentCameraX = Intent(this, CameraActivity::class.java)
        startActivity(intentCameraX)
    }

    private fun openNewCamera() {
        val intentCamera = Intent(this, NewCameraActivity::class.java)
        intentCamera.putExtra("workoutTitle", workoutTitle)
        intentCamera.putExtra("workoutDuration", workoutDuration)
        startActivity(intentCamera)
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

    companion object {
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}