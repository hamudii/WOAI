package com.bangkit.woai.views.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.woai.data.HistoryTraining
import com.bangkit.woai.data.WorkoutTraining
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.response.DataItemActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userActivityList = MutableLiveData<List<DataItemActivity>>()
    val userActivityList: LiveData<List<DataItemActivity>> get() = _userActivityList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val _historyTrainingList = MutableLiveData<List<HistoryTraining>>()
    val historyTrainingList: LiveData<List<HistoryTraining>> get() = _historyTrainingList


    fun getUserActivity(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val userActivityResponse = withContext(Dispatchers.IO) {
                    userRepository.getUserActivity(userId, token)
                }
                userActivityResponse?.let {
                    val dataList = it.data
                    dataList?.let {
                        _userActivityList.value = dataList?.filterNotNull() ?: emptyList()
                        val historyTrainingList = dataList?.mapNotNull { dataItemActivity ->
                            mapUserActivityToHistoryTraining(dataItemActivity ?: return@mapNotNull null)
                        } ?: emptyList()
                        _historyTrainingList.value = historyTrainingList
                        // sortHistoryTrainingList()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error retrieving user data"
            }
        }
    }

    fun mapUserActivityToHistoryTraining(dataItemActivity: DataItemActivity): HistoryTraining {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val createdAtDate = dateFormat.parse(dataItemActivity.createdAt ?: "")
        val formattedDate = dateFormat.format(createdAtDate ?: "")

        return HistoryTraining(
            id = dataItemActivity.id,
            date = formattedDate,
            time = "${dataItemActivity.duration} second",
        )
    }

    // fun sortHistoryTrainingList() {
    //     _historyTrainingList.value = _historyTrainingList.value?.sortedByDescending { it.date }
    // }
}
