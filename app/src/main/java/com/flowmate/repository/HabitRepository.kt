package com.flowmate.repository

import com.flowmate.ui.component.Habit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HabitRepository {
    private val habits = mutableListOf<Habit>()
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllHabits(): List<Habit> {
        return habits
    }

    fun addHabit(habit: Habit) {
        habits.add(habit)
    }

    fun removeHabit(habitId: String) {
        habits.removeAll { it.id == habitId }
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

    suspend fun addHabitToFirestore(userId: String, habit: Habit) {
        val habitMap = hashMapOf(
            "habitId" to habit.id,
            "userId" to userId,
            "title" to habit.title,
            "recurrence" to habit.frequency,
            "reminderTime" to habit.reminderTime,
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
                weeklyProgress = 0f, // Firestore'dan çekmek isterseniz ekleyin
                isCompletedToday = ((doc.get("completedDates") as? List<Long>)?.contains(java.time.LocalDate.now().toEpochDay())) == true,
                hardnessLevel = (doc.getLong("difficultyLevel") ?: 1L).toInt(),
                frequency = doc.getString("recurrence") ?: "",
                reminderEnabled = doc.get("reminderTime") != null,
                reminderTime = doc.getString("reminderTime")
            )
        }
    }

    suspend fun markHabitCompletedForToday(userId: String, habitId: String) {
        if (habitId.isBlank()) return // habitId boşsa işlem yapma
        val today = java.time.LocalDate.now().toEpochDay()
        val habitRef = firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(habitRef)
            val completedDates = (snapshot.get("completedDates") as? List<Long>)?.toMutableList() ?: mutableListOf()
            if (!completedDates.contains(today)) {
                completedDates.add(today)
                transaction.update(habitRef, "completedDates", completedDates)
            }
        }.await()
    }
}
