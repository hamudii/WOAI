package com.bangkit.woai.data

import java.io.Serializable

data class WorkoutTraining(
    val id: Int,
    val title: String,
    val imageResId: Int,
    val description: String,
    val duration: Int,
    val kcal: Int?
) : Serializable