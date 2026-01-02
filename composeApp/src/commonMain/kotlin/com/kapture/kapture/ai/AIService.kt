package com.kapture.kapture.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.String

class AIService(private val apiKey: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    suspend fun getSuggestion(): String {

        val prompt: String =
            "You are a creative brainstorming assistant. Based on the user's context and draft, generate a single, high-quality project or content idea.\n" +
            "\n" +
            "CRITICAL FORMATTING RULE: > Return the response in exactly this format: [Short Catchy Title]; [A 2-3 sentence description explaining the idea and why it works].\n" +
            "\n" +
            "Do not include any introductory text, quotes, or conversational filler. Only provide the Title; Description"

        val response: ChatResponse = client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                ChatRequest(
                    model = "gpt-4o-mini",
                    messages = listOf(
                        ChatMessage("system", prompt)
                    )
                )
            )
        }.body()

        if (response.error != null) {
            throw Exception("OpenAI Error: ${response.error.message}")
        }

        val suggestion = response.choices.firstOrNull()?.message?.content

        if (suggestion.isNullOrBlank()) {
            throw Exception("No response from AI: The choices list was empty.")
        }

        return suggestion
    }

}