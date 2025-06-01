package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.AIRepository
import com.flowmate.BuildConfig
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.SmartSuggestion
import com.flowmate.repository.HabitRepository

class MyHabitsViewModal : ViewModel() {

    // Alışkanlık listesi
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    // Öneriler (stub üzerinden gelenler)
    private val _habitSuggestions = MutableStateFlow<List<SmartSuggestion>>(emptyList())
    val habitSuggestions: StateFlow<List<SmartSuggestion>> = _habitSuggestions.asStateFlow()

    // Gemini AI'den gelen metin çıktısı
    private val _parsedSuggestions = MutableStateFlow<List<SmartSuggestion>>(emptyList())
    val parsedSuggestions: StateFlow<List<SmartSuggestion>> = _parsedSuggestions


    private val repo = AIRepository()

    init {
        loadStubData()
    }

    private fun loadStubData() {
        _habits.value = listOf(
            Habit("1", "Morning Run", 0.3f, isCompletedToday = false, hardnessLevel = 5),
            Habit("2", "Read a Book", 0.6f, isCompletedToday = true, hardnessLevel = 4)
        )
        _habitSuggestions.value = listOf(
            SmartSuggestion("s1", "Try a 5-minute journaling habit"),
            SmartSuggestion("s2", "Add a midday stretch break")
        )
    }

    fun toggleHabitCompletion(habitId: String) {
        val updated = _habits.value.map { habit ->
            if (habit.id == habitId) {
                habit.copy(
                    isCompletedToday = !habit.isCompletedToday,
                    weeklyProgress = if (!habit.isCompletedToday)
                        (habit.weeklyProgress + 1f / 7f).coerceAtMost(1f)
                    else
                        (habit.weeklyProgress - 1f / 7f).coerceAtLeast(0f)
                )
            } else habit
        }
        _habits.value = updated
    }

    fun addHabit(habit: Habit) {
        _habits.value = _habits.value + habit
    }
    val apiKey = BuildConfig.GEMINI_API_KEY

    private val _rawAiSuggestions = MutableStateFlow("")
    val rawAiSuggestions: StateFlow<String> = _rawAiSuggestions

    fun loadUserHabits(userId: String) {
        viewModelScope.launch {
            val repo = AIRepository() // veya HabitRepository varsa onu kullan
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("users").document(userId).collection("habits")
                .get()
                .addOnSuccessListener { snapshot ->
                    val habits = snapshot.documents.mapNotNull { doc ->
                        val id = doc.id
                        val title = doc.getString("title") ?: return@mapNotNull null
                        val hardness = (doc.get("difficultyLevel") as? Long)?.toInt() ?: 1
                        Habit(id, title, 0f, false, hardness)
                    }
                    _habits.value = habits
                }
        }
    }

    fun fetchSuggestions(userId: String) {
        viewModelScope.launch {
            loadUserHabits(userId)
            val userHabits = _habits.value
            val habitNames = userHabits.joinToString(", ") { it.title }
            val prompt = "The user's current habits are: $habitNames. Considering these, suggest 5 new habits in English. List only the suggestions and their explanation very shortly."
            val response = repo.getSuggestions(prompt, BuildConfig.GEMINI_API_KEY)
            _rawAiSuggestions.value = response

            val suggestions = response
                .replace("**", "")
                .split("\n")
                .filter { it.isNotBlank() }
                .take(5)
                .mapIndexed { idx, line ->
                    val parts = line.split(":", limit = 2)
                    val title = parts.getOrNull(0)?.trim() ?: "Suggestion ${idx + 1}"
                    val text = parts.getOrNull(1)?.trim() ?: line.trim()
                    SmartSuggestion("s$idx", "$title: $text")
                }

            _parsedSuggestions.value = suggestions
            android.util.Log.d("ReportsScreen", "AI suggestions fetched for user: $habitNames")
            android.util.Log.d("ReportsScreen", "Habits: $habits")
            android.util.Log.d("ReportsScreen", "Summary prompt:$response")
        }
    }


}
