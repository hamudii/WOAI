package com.bangkit.woai.data.response

import com.google.gson.annotations.SerializedName

data class UserActivityResponse(

	@field:SerializedName("data")
	val data: List<DataItemActivity?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItemActivity(

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("correct")
	val correct: Int? = null,

	@field:SerializedName("incorrect")
	val incorrect: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("up_correct")
	val upCorrect: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("down_correct")
	val downCorrect: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null
)
