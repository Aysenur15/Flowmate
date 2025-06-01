package com.flowmate.worker

import android.content.Context
import com.flowmate.ui.component.HabitType
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime


object ReminderScheduler {
// Schedules a reminder if the feature is enabled and the target time is in the future.
    fun scheduleReminderIfEnabled(
        context: Context,
        title: String,
        targetTime: LocalDateTime,
        isEnabled: Boolean,
        type: HabitType,
        time: LocalTime
    )

    {
        if (!isEnabled) return

        val delay = Duration.between(LocalDateTime.now(), targetTime).toMillis()
        if (delay > 0) {
            ReminderWorker.enqueueReminder(context, title, delay, type, time)
        }

    }

}
