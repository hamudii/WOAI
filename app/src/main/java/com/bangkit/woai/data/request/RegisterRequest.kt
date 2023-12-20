package com.bangkit.woai.data.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("weight")
	val weight: Int? = null,

	@field:SerializedName("height")
	val height: Int? = null,

	@field:SerializedName("password_hash")
	val passwordHash: String? = null
)
