package com.flowmate.repository

import com.flowmate.data.remote.*

class AIRepository {
    // This class handles AI-related operations, such as generating content suggestions using Gemini API.
    suspend fun getSuggestions(prompt: String, apiKey: String): String {
        return try {
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = GeminiService.api.generateContent("AIzaSyC4FcgTWZ0To8YCg0dEqIyqwh_Pg-L5LeI", request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Cannot get suggestion."
        } catch (e: Exception) {
            e.printStackTrace()
            "AI prompt failed: ${e.message}"
        }
    }

}
