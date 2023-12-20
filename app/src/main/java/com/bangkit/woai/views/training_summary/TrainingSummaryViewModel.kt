package com.bangkit.woai.views.training_summary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.response.SpecificActivityResponse
import kotlinx.coroutines.launch

class TrainingSummaryViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _specificActivity = MutableLiveData<SpecificActivityResponse>()
    val specificActivity: LiveData<SpecificActivityResponse> get() = _specificActivity

    fun getSpecificActivity(historyTrainingId: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getSpecificActivity(historyTrainingId, token)
                _specificActivity.postValue(response)
                Log.d("TrainingSummaryViewModel", "Specific Activity Data: $response")
            } catch (e: Exception) {
                // Handle error
                Log.e("TrainingSummaryViewModel", "Error getting specific activity: ${e.message}")
            }
        }
    }
}