package com.bangkit.woai.views.authentication.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.response.RegisterResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    val registrationStatus: MutableLiveData<Result<RegisterResponse>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun register(name: String, email: String, height: Int, weight: Int, password_hash: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = userRepository.registerUser(name, email, height, weight, password_hash)
                if (response.message == "user successfully created") {
                    registrationStatus.postValue(Result.success(response))
                } else {
                    val errorBody = response.message ?: "Terjadi kesalahan saat mendaftar!"
                    registrationStatus.postValue(Result.failure(Exception(errorBody)))
                    errorMessage.postValue(errorBody)
                }
            } catch (e: HttpException) {
                val errorBodyString = e.response()?.errorBody()?.string()
                try {
                    val jsonObject = JSONObject(errorBodyString)
                    val extractedMessage = jsonObject.getString("message")
                    this@RegisterViewModel.errorMessage.postValue(extractedMessage)
                } catch (ex: Exception) {
                    // Jika terjadi kesalahan saat parsing JSON atau key "message" tidak ditemukan.
                    this@RegisterViewModel.errorMessage.postValue("Terjadi kesalahan saat mendaftar!")
                }
                isLoading.postValue(false)
            } catch (e: Exception) {
                // Handle other errors if needed
                registrationStatus.postValue(Result.failure(e))
                isLoading.postValue(false)
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}