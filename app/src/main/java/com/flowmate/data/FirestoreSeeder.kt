package com.flowmate.data

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object FirestoreSeeder {
    fun seedFirestoreWithSampleData() {
        // FirestoreSeeder.kt
        val db = FirebaseFirestore.getInstance()
        val userId = "leJ77vgw5pYlCj0fawhOVwoTJqx1"
        val habitId = "habit_001"

// Örnek zaman segmentleri (son 7 gün için)
        val today = Calendar.getInstance()
        for (i in 0..6) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = today.timeInMillis
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            val minutes = (10..60).random() // Rastgele süre

            val timeEntry = hashMapOf(
                "date" to dateStr,
                "minutes" to minutes
            )

            db.collection("users")
                .document(userId)
                .collection("habits")
                .document(habitId)
                .collection("habitTimes")
                .document(dateStr)
                .set(timeEntry)
        }
    }
}