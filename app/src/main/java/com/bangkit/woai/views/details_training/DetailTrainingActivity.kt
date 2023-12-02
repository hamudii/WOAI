package com.bangkit.woai.views.details_training

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bangkit.woai.R
import com.bangkit.woai.databinding.ActivityDetailTrainingBinding
import com.bangkit.woai.views.camera.CameraActivity

class DetailTrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTrainingBinding

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
                openCamera()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun openCamera() {
        val intentCameraX = Intent(this, CameraActivity::class.java)
        startActivity(intentCameraX)
    }

    companion object {
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}