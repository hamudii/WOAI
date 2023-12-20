package com.bangkit.woai.data.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(

	@field:SerializedName("logged_out_status")
	val loggedOutStatus: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
