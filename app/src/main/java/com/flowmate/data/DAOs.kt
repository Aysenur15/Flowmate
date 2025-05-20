package com.flowmate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE userId = :userId")
    suspend fun getHabitsByUser(userId: String): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE habitId = :habitId")
    suspend fun getHabitById(habitId: String): HabitEntity?

    @Query("UPDATE habits SET completedDates = :completedDates WHERE habitId = :habitId")
    suspend fun updateHabitCompletion(habitId: String, completedDates: List<Long>)
}

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    suspend fun getTasksByUser(userId: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getRemindersByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteReminder(reminder: TimerLogEntity)
}

@Dao
interface SuggestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getSuggestionsByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteSuggestion(suggestion: TimerLogEntity)
}

@Dao
interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getMoodsByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteMood(mood: TimerLogEntity)
}

@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getTimersByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteTimer(timer: TimerLogEntity)
}

@Dao
interface TimerLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimerLog(timerLog: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getTimerLogsByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteTimerLog(timerLog: TimerLogEntity)
}

@Dao
interface AnalyticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(analytics: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getAnalyticsByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteAnalytics(analytics: TimerLogEntity)
}

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: TimerLogEntity)

    @Query("SELECT * FROM timer_logs WHERE userId = :userId")
    suspend fun getSettingsByUser(userId: String): List<TimerLogEntity>

    @Delete
    suspend fun deleteSettings(settings: TimerLogEntity)
}
