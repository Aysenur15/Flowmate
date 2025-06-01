package com.flowmate.repository

import com.flowmate.data.remote.*

class AIRepository {

    // 🟡 DİKKAT: API anahtarı burada açık şekilde yazılıyor (güvenlik riski!

    suspend fun getSuggestions(prompt: String, apiKey: String): String {
        return try {
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = GeminiService.api.generateContent("AIzaSyC4FcgTWZ0To8YCg0dEqIyqwh_Pg-L5LeI", request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "AI'den öneri alınamadı."
        } catch (e: Exception) {
            e.printStackTrace()
            "AI isteği başarısız oldu: ${e.message}"
        }
    }

}
