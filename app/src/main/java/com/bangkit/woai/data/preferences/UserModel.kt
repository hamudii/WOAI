package com.bangkit.woai.data.preferences

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean,
    val name: String
)