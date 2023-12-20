package com.bangkit.woai.views.authentication.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.woai.data.repository.UserRepository
import com.bangkit.woai.data.response.LoginResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    val loginStatus: MutableLiveData<Result<LoginResponse>> = MutableLiveData()
    private var token: String? = null
    private var name: String? = null
    private var id: Int? = null

    fun getToken(): String? {
        return token
    }

    fun getName(): String? {
        return name
    }

    fun getID(): String? {
        return id.toString()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(email, password)
                Log.d("Data Login", "API Response: $response")
                if (response.message.isNullOrEmpty()) {
                    token = response.token
                    name = response.name.toString()
                    id = response.id
                    Log.d("data ", "data: $id, $token")
                    loginStatus.postValue(Result.success(response))
                } else {
                    loginStatus.postValue(Result.failure(Exception("Token tidak ditemukan dalam respons login!")))
                }
            } catch (e: HttpException) {
                val errorBodyString = e.response()?.errorBody()?.string()
                try {
                    val jsonObject = JSONObject(errorBodyString)
                    val extractedMessage = jsonObject.getString("message")
                    loginStatus.postValue(Result.failure(Exception(extractedMessage)))
                } catch (ex: Exception) {
                    loginStatus.postValue(Result.failure(Exception("Terjadi kesalahan saat login!")))
                }
            } catch (e: Exception) {
                loginStatus.postValue(Result.failure(e))
            }
        }
    }
}
