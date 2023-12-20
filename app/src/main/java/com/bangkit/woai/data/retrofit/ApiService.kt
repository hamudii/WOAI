package com.bangkit.woai.data.retrofit

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ) : RegisterResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ) : LoginResponse

    @GET("user/{id}")
    suspend fun getUserData(
        @Path("id") id: Int,
        @Header("x-token") token: String
    ): DetailUserResponse

    @GET("activity/{id}")
    suspend fun getActivityUser(
        @Path("id") id: Int,
        @Header("x-token") token: String
    ): UserActivityResponse

    @POST("activity")
    suspend fun AddUserActivity(
        @Header("x-token") token: String,
        @Body request: TrainingActivityRequest
    ): TrainingActivityResponse

    @POST("logout")
    suspend fun logout(
        @Header("x-token") token: String,
    ): LogoutResponse

    @POST("activity/{id}")
    suspend fun deleteActivity(
        @Path("id") id: Int,
        @Header("x-token") token: String
    ): DeleteActivityResponse

    @GET("activity1/{id}")
    suspend fun getSpecificActivity(
        @Path("id") id: Int,
        @Header("x-token") token: String
    ): SpecificActivityResponse


}