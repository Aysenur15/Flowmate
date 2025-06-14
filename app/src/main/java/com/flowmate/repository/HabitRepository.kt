package com.flowmate.repository

import android.content.Context
import androidx.work.WorkManager
import com.flowmate.ui.component.Habit
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HabitRepository {
    // Singleton instance
    private val habits = mutableListOf<Habit>()
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllHabits(): List<Habit> {
        return habits
    }

    fun addHabit(habit: Habit) {
        habits.add(habit)
    }

    fun removeHabit(context: Context, habitId: String) {
        val removedHabit = habits.find { it.id == habitId }
        if (removedHabit != null) {
            WorkManager.getInstance(context).cancelAllWorkByTag(removedHabit.title)
            habits.remove(removedHabit)
        }
    }

    fun updateHabit(updatedHabit: Habit) {
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
        }
    }

    fun getHabitById(habitId: String): Habit? {
        return habits.find { it.id == habitId }
    }
    // Firestore works
    suspend fun addHabitToFirestore(userId: String, habit: Habit) {
        val habitMap = hashMapOf(
            "habitId" to habit.id,
            "userId" to userId,
            "title" to habit.title,
            "recurrence" to habit.frequency,
            "reminderTime" to habit.reminderTime,
            "reminderEnabled" to habit.reminderEnabled,
            "createdAt" to System.currentTimeMillis(),
            "completedDates" to (emptyList<Long>()),
            "difficultyLevel" to habit.hardnessLevel
        )
        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.id)
            .set(habitMap)
            .await()
    }

    suspend fun getHabitsFromFirestore(userId: String): List<Habit> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            Habit(
                id = doc.getString("habitId") ?: "",
                title = doc.getString("title") ?: "",
                weeklyProgress = 0f,
                isCompletedToday = ((doc.get("completedDates") as? List<Long>)?.contains(java.time.LocalDate.now().toEpochDay())) == true,
                hardnessLevel = (doc.getLong("difficultyLevel") ?: 1L).toInt(),
                frequency = doc.getString("recurrence") ?: "",
                reminderEnabled = doc.getBoolean("reminderEnabled") ?: false,
                reminderTime = doc.getString("reminderTime"),
                completedDates = doc.get("completedDates") as? List<Long> ?: emptyList()
            )
        }
    }

    suspend fun markHabitCompletedForToday(userId: String, habitId: String) {
        if (habitId.isBlank()) return
        val today = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val habitRef = firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
        println("Firestore updates: userId=$userId, habitId=$habitId, today=$today")
        try {
            habitRef.update("completedDates", FieldValue.arrayUnion(today)).await()
            println("Firestore updated successfully.")
        } catch (e: Exception) {
            println("Firestore update error: ${e.message}")
        }
    }

    fun cancelReminderForHabit(context: Context, habitTitle: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(habitTitle)
    }
    //Delete habit completely
    suspend fun deleteHabitCompletely(context: Context, userId: String, habit: Habit) {
        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.id)
            .delete()
            .await()

        // WorkManager reminder cancel
        WorkManager.getInstance(context).cancelAllWorkByTag(habit.title)

        // Remove from local list
        habits.removeAll { it.id == habit.id }
    }
    // Update habit frequency
    suspend fun updateHabitFrequency(userId: String, habitId: String, newFrequency: String) {
        if (habitId.isBlank()) return
        val habitRef = firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
        try {
            habitRef.update("recurrence", newFrequency).await()
        } catch (e: Exception) {
            println("Firestore update error (recurrence): ${e.message}")
        }
    }
    // Calculate streak based on completed dates
    fun calculateStreak(completedDates: List<Long>): Int {
        if (completedDates.isEmpty()) return 0
        val today = java.time.LocalDate.now()
        val sortedDates = completedDates.map {
            java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        }.sortedDescending()
        var streak = 0
        var current = today
        for (date in sortedDates) {
            if (date == current) {
                streak++
                current = current.minusDays(1)
            } else if (date.isBefore(current)) {
                break
            }
        }
        return streak
    }
    // Add habit time entry to Firestore
    suspend fun addHabitTimeToFirestore(
        userId: String,
        habitId: String,
        date: String, // Date format: "yyyy-MM-dd"
        minutes: Int, //Time spent in minutes
        moodNote: String = ""
    ) {
        val timeEntry = hashMapOf(
            "date" to date,
            "minutes" to minutes,
            "moodNote" to moodNote
        )
        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
            .collection("habitTimes")
            .document(date)
            .set(timeEntry)
            .await()
    }
}
