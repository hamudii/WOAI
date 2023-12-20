package com.bangkit.woai.views.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.request.TrainingActivityRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun addUserActivity(token: String, trainingActivityRequest: TrainingActivityRequest) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = userRepository.addUserActivity(token, trainingActivityRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}