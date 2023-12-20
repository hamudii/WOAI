package com.bangkit.woai.data.repository

import com.bangkit.woai.data.preferences.UserModel
import com.bangkit.woai.data.preferences.UserPreference
import com.bangkit.woai.data.request.LoginRequest
import com.bangkit.woai.data.request.RegisterRequest
import com.bangkit.woai.data.request.TrainingActivityRequest
import com.bangkit.woai.data.response.DeleteActivityResponse
import com.bangkit.woai.data.response.DetailUserResponse
import com.bangkit.woai.data.response.LoginResponse
import com.bangkit.woai.data.response.LogoutResponse
import com.bangkit.woai.data.response.RegisterResponse
import com.bangkit.woai.data.response.SpecificActivityResponse
import com.bangkit.woai.data.response.TrainingActivityResponse
import com.bangkit.woai.data.response.UserActivityResponse
import com.bangkit.woai.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val apiService: ApiService,
){
    suspend fun registerUser(name: String, email: String, height: Int, weight: Int, password_hash: String): RegisterResponse {
        return apiService.register(
            RegisterRequest(name, email, height, weight, password_hash)
        )
    }


    suspend fun loginUser(email: String, password_hash: String): LoginResponse {
        return apiService.login(
            LoginRequest(email, password_hash)
        )

    }

    suspend fun getUserActivity(id: Int, token: String): UserActivityResponse{
        return apiService.getActivityUser(id,token)
    }

    suspend fun getSpecificActivity(id: Int, token: String): SpecificActivityResponse{
        return apiService.getSpecificActivity(id,token)
    }

    suspend fun getUserData(id: Int, token: String): DetailUserResponse {
        return apiService.getUserData(id,token)
    }

    suspend fun deleteActivity(id: Int, token: String): DeleteActivityResponse{
        return apiService.deleteActivity(id, token)
    }


    suspend fun addUserActivity(token: String, request: TrainingActivityRequest): TrainingActivityResponse {
        return apiService.AddUserActivity(token, request)
    }

    suspend fun logout(token: String): LogoutResponse{
        return apiService.logout(token)
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }

}