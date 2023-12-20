package com.bangkit.woai.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("weight")
    val weight: Int? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("height")
    val height: Int? = null
)
