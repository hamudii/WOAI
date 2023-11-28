package com.bangkit.woai.data

import com.bangkit.woai.R

object DummyData {
    val workoutTrainings = listOf(
        WorkoutTraining("5 Minute Push-Up", R.drawable.get_started),
        WorkoutTraining("10 Minute Push-Up", R.drawable.get_started_2),
        WorkoutTraining("Unlimited Push-Up", R.drawable.get_started_3),
    )

    val historyTrainings = listOf(
        HistoryTraining("15 November 2023", "10:00"),
        HistoryTraining("16 November 2023", "00:30"),
        HistoryTraining("17 November 2023", "01:20"),
        HistoryTraining("15 November 2023", "10:00"),
        HistoryTraining("16 November 2023", "00:30"),
        HistoryTraining("17 November 2023", "01:20"),
        HistoryTraining("15 November 2023", "10:00"),
        HistoryTraining("16 November 2023", "00:30"),
        HistoryTraining("17 November 2023", "01:20"),
    )
}