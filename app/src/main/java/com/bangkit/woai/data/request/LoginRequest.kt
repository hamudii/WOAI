package com.bangkit.woai.data.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("password_hash")
    val passwordHash: String? = null
)
