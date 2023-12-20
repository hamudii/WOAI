package com.bangkit.woai.data.response

import com.google.gson.annotations.SerializedName

data class TrainingActivityResponse(

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

	@field:SerializedName("down_correct")
	val downCorrect: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)
