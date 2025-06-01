package com.flowmate.viewmodel

import com.flowmate.ui.component.Habit
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MyHabitsViewModalTest {
    private lateinit var viewModel: MyHabitsViewModal

    @Before
    fun setUp() {
        viewModel = MyHabitsViewModal()
    }

    @Test
    fun testAddHabit() {
        val habit = Habit("3", "Test Habit", 0f, false, 2)
        viewModel.addHabit(habit)
        val habits = viewModel.habits.value
        assert(habits.any { it.id == "3" && it.title == "Test Habit" })
    }

    @Test
    fun testToggleHabitCompletion() {
        val habitId = viewModel.habits.value.first().id
        val initial = viewModel.habits.value.first { it.id == habitId }.isCompletedToday
        viewModel.toggleHabitCompletion(habitId)
        val updated = viewModel.habits.value.first { it.id == habitId }.isCompletedToday
        assertEquals(!initial, updated)
    }

    // Test for AI suggestions stub meaning it uses hardcoded data
    @Test
    fun testStubAiSuggestions() {
        val suggestions = viewModel.habitSuggestions.value
        assert(suggestions.isNotEmpty())
        assert(suggestions.any { it.text.contains("journaling") })
    }

    @Test
    fun testParsedAiSuggestionsStub() {
        val parsed = viewModel.parsedSuggestions.value
        assert(parsed.isEmpty())
    }
}
