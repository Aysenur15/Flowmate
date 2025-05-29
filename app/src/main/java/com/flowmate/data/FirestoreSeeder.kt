package com.flowmate.data

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreSeeder {
    fun seedFirestoreWithSampleData() {
        val db = FirebaseFirestore.getInstance()
        val userId = "leJ77vgw5pYlCj0fawhOVwoTJqx1"

        // Habit with difficulty and completedDates
        val habit = hashMapOf(
            "habitId" to "habit_001",
            "userId" to userId,
            "title" to "Morning Run",
            "recurrence" to "daily",
            "reminderTime" to null,
            "createdAt" to 1716979200000,
            "completedDates" to listOf(
                1716768000000, // May 27
                1716854400000  // May 28
            ),
            "difficultyLevel" to "hard"
        )
        db.collection("habits").document("habit_001").set(habit)

// Additional habits for Pie Chart
        val habit2 = hashMapOf(
            "habitId" to "habit_002",
            "userId" to userId,
            "title" to "Read 10 Pages",
            "recurrence" to "daily",
            "createdAt" to 1716979200000,
            "completedDates" to listOf(1716854400000),
            "difficultyLevel" to "medium"
        )
        db.collection("habits").document("habit_002").set(habit2)

        val habit3 = hashMapOf(
            "habitId" to "habit_003",
            "userId" to userId,
            "title" to "Meditate",
            "recurrence" to "daily",
            "createdAt" to 1716979200000,
            "completedDates" to listOf(1716854400000),
            "difficultyLevel" to "easy"
        )
        db.collection("habits").document("habit_003").set(habit3)

// Habit Duration Summary (Analytics-style)
        val dailySummary1 = hashMapOf(
            "userId" to userId,
            "habitId" to "habit_001",
            "date" to "2025-05-28",
            "duration" to 2700000 // 45 mins
        )
        db.collection("dailyHabitSummary").add(dailySummary1)

        val dailySummary2 = hashMapOf(
            "userId" to userId,
            "habitId" to "habit_002",
            "date" to "2025-05-28",
            "duration" to 1080000 // 18 mins
        )
        db.collection("dailyHabitSummary").add(dailySummary2)

        val dailySummary3 = hashMapOf(
            "userId" to userId,
            "habitId" to "habit_003",
            "date" to "2025-05-28",
            "duration" to 1620000 // 27 mins
        )
        db.collection("dailyHabitSummary").add(dailySummary3)

// Extended Analytics with AI insights
        val analytics = hashMapOf(
            "analyticsId" to "ana_001",
            "userId" to userId,
            "bestTime" to "morning",
            "focusStrategy" to "afternoon",
            "streakCount" to 10,
            "date" to 1716979200000,
            "completionStatus" to true
        )
        db.collection("analytics").document("ana_001").set(analytics)
    }
}