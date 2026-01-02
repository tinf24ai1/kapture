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
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    suspend fun getSuggestion(): GeminiResponse {

        val prompt: String =
            "You are a creative brainstorming assistant. Based on the user's context and draft, generate a single, high-quality project or content idea.\n" +
            "\n" +
            "CRITICAL FORMATTING RULE: > Return the response in exactly this format: [Short Catchy Title]; [A 2-3 sentence description explaining the idea and why it works].\n" +
            "\n" +
            "Do not include any introductory text, quotes, or conversational filler. Only provide the Title; Description"

        val response: GeminiResponse = client.post("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    contents = listOf(
                        Content(
                            listOf(
                                Part(text = prompt)
                            )
                        )
                    )
                )
            )
        }.body()

        return response
    }

}