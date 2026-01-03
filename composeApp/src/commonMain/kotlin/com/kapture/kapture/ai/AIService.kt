package com.kapture.kapture.ai

import com.kapture.kapture.logger.Logger
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

    suspend fun getSuggestion(title: String, desc: String): GeminiResponse {

        val prompt: String =
            "Act as a creative brainstorming assistant. Your goal is to generate one highly realistic, small-scale project idea that a regular person could feasibly complete in a weekend with little to no budget. The idea must be practical, grounded in daily life, and avoid overly ambitious or complex \"startup-style\" concepts.\n" +
            "\n" +
            "If the user provides a Title and Description, ONLY REWRITE and simplify them into a manageable, actionable project. If no input is provided, generate a completely new, low-barrier idea.\n" +
            "\n" +
            "POTENTIAL USER INPUT: TITLE: $title\n" +
            "POTENTIAL USER INPUT: DESC:  $desc\n" +
            "\n" +
            "CRITICAL FORMATTING RULE: > Return the response in exactly this format: [Short Catchy Title]; [A 1 sentence description explaining the idea and why it works].\n" +
            "\n" +
            "Constraint: Do not include any introductory text, quotes, or conversational filler. Only provide the Title; Description."

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

        Logger.d("Prompt", prompt)

        return response
    }

}