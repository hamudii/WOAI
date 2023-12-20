package com.bangkit.woai.data

import com.bangkit.woai.R

object DummyData {
    val workoutTrainings = listOf(
        WorkoutTraining(1, "1 Minute Push-Up", R.drawable.get_started, "Push up: Latihan tubuh atas tanpa peralatan. Menguatkan dada, bahu, dan trisep. Sederhana dan dapat dilakukan di mana saja.", 60, 7),
        WorkoutTraining(2, "2 Minute Push-Up", R.drawable.get_started_2, "Push up: Latihan tubuh atas tanpa peralatan. Menguatkan dada, bahu, dan trisep. Sederhana dan dapat dilakukan di mana saja.", 120, 14),
        WorkoutTraining(3, "3 Minute Push-Up", R.drawable.get_started_3, "Push up: Latihan tubuh atas tanpa peralatan. Menguatkan dada, bahu, dan trisep. Sederhana dan dapat dilakukan di mana saja.", 180, 21),
    )

    val historyTrainings = listOf(
        HistoryTraining(null,"No Data", "-- : --",)
    )
}