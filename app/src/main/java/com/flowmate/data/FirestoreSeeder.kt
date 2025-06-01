package com.flowmate.data

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object FirestoreSeeder {
    // This function seeds the Firestore database with sample data for testing purposes.
    fun seedFirestoreWithSampleData() {
        val db = FirebaseFirestore.getInstance()
        val userId = "leJ77vgw5pYlCj0fawhOVwoTJqx1"

        // Habit 1 - Hard
        val habit1 = hashMapOf(
            "habitId" to "habit_001",
            "userId" to userId,
            "title" to "Morning Run",
            "difficultyLevel" to 5, //Indicates a hard habit
            "completedDates" to listOf(
                1716768000000, // May 27
                1716854400000  // May 28
            )
        )
        db.collection("habits").document("habit_001").set(habit1)

        // Habit 2 - Medium
        val habit2 = hashMapOf(
            "habitId" to "habit_002",
            "userId" to userId,
            "title" to "Read 10 Pages",
            "difficultyLevel" to 3,
            "completedDates" to listOf(1716854400000)
        )
        db.collection("habits").document("habit_002").set(habit2)

        // Habit 3 - Easy
        val habit3 = hashMapOf(
            "habitId" to "habit_003",
            "userId" to userId,
            "title" to "Meditate",
            "difficultyLevel" to 1,
            "completedDates" to listOf(1716854400000)
        )
        db.collection("habits").document("habit_003").set(habit3)

        // Time entries for the last 7 days
        val today = Calendar.getInstance()
        for (i in 0..6) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = today.timeInMillis
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            val minutes = (10..60).random()

            val timeEntry = hashMapOf(
                "date" to dateStr,
                "minutes" to minutes
            )

            db.collection("users")
                .document(userId)
                .collection("habits")
                .document("habit_001")
                .collection("habitTimes")
                .document(dateStr)
                .set(timeEntry)
        }
    }


}