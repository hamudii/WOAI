package com.bangkit.woai.views.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.woai.data.HistoryTraining
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.response.DataItemActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel(private val userRepository: UserRepository):ViewModel() {
    private val _userActivityList = MutableLiveData<List<DataItemActivity>>()
    val userActivityList: LiveData<List<DataItemActivity>> get() = _userActivityList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val _historyTrainingList = MutableLiveData<List<HistoryTraining>>()
    val historyTrainingList: LiveData<List<HistoryTraining>> get() = _historyTrainingList
    private val _emptyState = MutableLiveData<Boolean>()
    val emptyState: LiveData<Boolean> get() = _emptyState


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
                    }
                    _emptyState.postValue(dataList.isNullOrEmpty())
                }
            } catch (e: Exception) {
                _emptyState.postValue(true)
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

    fun deleteActivity(userId: Int, token: String){
        viewModelScope.launch {
            try {
                val deleteResponse = withContext(Dispatchers.IO) {
                    userRepository.deleteActivity(userId, token)
                }
                deleteResponse?.let {
                }
            } catch (e: Exception) {
            }
        }
    }
}
