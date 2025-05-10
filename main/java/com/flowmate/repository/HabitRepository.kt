package com.flowmate.repository

import com.flowmate.ui.component.Habit

class HabitRepository {
    private val habits = mutableListOf<Habit>()

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
}