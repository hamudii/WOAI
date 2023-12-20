package com.bangkit.woai.views.training_summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.views.main.MainViewModel

class TrainingSummaryViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainingSummaryViewModel::class.java)) {
            return TrainingSummaryViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}