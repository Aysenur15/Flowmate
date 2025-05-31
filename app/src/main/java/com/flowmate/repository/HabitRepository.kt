package com.flowmate.repository

import android.content.Context
import androidx.work.WorkManager
import com.flowmate.ui.component.Habit
import com.google.firebase.firestore.FieldValue
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
        println("Firestore güncelleme deneniyor: userId=$userId, habitId=$habitId, today=$today")
        try {
            habitRef.update("completedDates", FieldValue.arrayUnion(today)).await()
            println("Firestore BAŞARILI şekilde güncellendi.")
        } catch (e: Exception) {
            println("Firestore update HATASI: ${e.message}")
        }
    }
    fun cancelReminderForHabit(context: Context, habitTitle: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(habitTitle)
    }
    suspend fun deleteHabitCompletely(context: Context, userId: String, habit: Habit) {
        // 1. Firestore'dan sil
        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.id)
            .delete()
            .await()

        // 2. WorkManager hatırlatıcılarını iptal et
        WorkManager.getInstance(context).cancelAllWorkByTag(habit.title)

        // 3. Local list'ten kaldır (opsiyonel, genelde Flow güncellenir zaten)
        habits.removeAll { it.id == habit.id }
    }


}
