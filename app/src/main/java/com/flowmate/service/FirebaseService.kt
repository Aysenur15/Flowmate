package com.flowmate.service

import com.flowmate.data.HabitDao
import com.flowmate.data.HabitEntity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await


class FirebaseService(private val firestore: FirebaseFirestore) {

    suspend fun uploadHabit(habit: HabitEntity) {
        val docRef = firestore.collection("users")
            .document(habit.userId)
            .collection("habits")
            .document(habit.habitId)

        val habitMap = hashMapOf(
            "habitId" to habit.habitId,
            "userId" to habit.userId,
            "title" to habit.title,
            "recurrence" to habit.recurrence,
            "reminderTime" to habit.reminderTime,
            "createdAt" to habit.createdAt,
            "completedDates" to habit.completedDates,
            "syncedAt" to FieldValue.serverTimestamp(),
            "isDeleted" to false
        )

        docRef.set(habitMap, SetOptions.merge()).await()
    }

    suspend fun deleteHabit(userId: String, habitId: String) {
        val docRef = firestore.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)

        docRef.update("isDeleted", true, "syncedAt", FieldValue.serverTimestamp()).await()
    }

    suspend fun getHabits(userId: String): List<HabitEntity> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("habits")
            .whereEqualTo("isDeleted", false)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val map = doc.data ?: return@mapNotNull null
            HabitEntity(
                habitId = map["habitId"] as String,
                userId = map["userId"] as String,
                title = map["title"] as String,
                recurrence = map["recurrence"] as String,
                reminderTime = map["reminderTime"] as? Long,
                createdAt = map["createdAt"] as Long,
                completedDates = (map["completedDates"] as? List<*>)
                    ?.mapNotNull { it as? Long } ?: emptyList()

            )
        }
    }

    suspend fun syncHabits(userId: String, localHabits: List<HabitEntity>, habitDao: HabitDao) {
        val remoteHabits = getHabits(userId)

        // Remote -> Local Sync
        remoteHabits.forEach { remote ->
            val local = localHabits.find { it.habitId == remote.habitId }
            if (local == null || remote.createdAt > local.createdAt) {
                habitDao.insertHabit(remote)
            }
        }

        // Local -> Remote Sync
        localHabits.forEach { local ->
            val remote = remoteHabits.find { it.habitId == local.habitId }
            if (remote == null || local.createdAt > remote.createdAt) {
                uploadHabit(local)
            }
        }
    }
}
