package com.flowmate.worker

import android.content.Context
import androidx.work.*
import com.flowmate.ui.component.HabitType
import com.flowmate.util.NotificationHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import java.time.Duration

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("habit_title") ?: "Habit Reminder"
        val typeString = inputData.getString("habit_type") ?: "DAILY"
        val timeString = inputData.getString("reminder_time") ?: "08:00"

        NotificationHelper.showNotification(applicationContext, title)

        // Tekrar için planlama
        val type = HabitType.valueOf(typeString)
        val reminderTime = LocalTime.parse(timeString)
        val now = LocalDateTime.now()

        val nextDateTime = when (type) {
            HabitType.DAILY -> now.plusDays(1)
            HabitType.WEEKLY -> now.plusWeeks(1)
            HabitType.MONTHLY -> now.plusMonths(1)
            HabitType.YEARLY -> now.plusYears(1)
        }.withHour(reminderTime.hour).withMinute(reminderTime.minute).withSecond(0).withNano(0)

        val delay = Duration.between(now, nextDateTime).toMillis()

        // Kendisini tekrar planla
        enqueueReminder(applicationContext, title, delay, type, reminderTime)

        return Result.success()
    }

    companion object {
        fun enqueueReminder(
            context: Context,
            title: String,
            delayMillis: Long,
            type: HabitType,
            reminderTime: LocalTime
        ) {
            val data = workDataOf(
                "habit_title" to title,
                "habit_type" to type.name,
                "reminder_time" to reminderTime.toString()
            )

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(title)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }

    }
    private fun enqueueReminder(
        context: Context,
        title: String,
        delayMillis: Long,
        type: HabitType,
        reminderTime: LocalTime
    ) {
        val data = workDataOf(
            "habit_title" to title,
            "habit_type" to type.name,
            "reminder_time" to reminderTime.toString()
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(title)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            title, // title is used as unique ID
            ExistingWorkPolicy.REPLACE, // replaces any previous job with the same title
            request
        )
    }

}
