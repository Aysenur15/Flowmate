package com.flowmate.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Request Structure ---
data class GeminiRequest(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(val text: String)

// --- Response Structure ---
data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: CandidateContent)
data class CandidateContent(val parts: List<Part>)


