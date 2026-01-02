package com.kapture.kapture.ai

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
data class OpenAIError(
    val message: String,
    val type: String? = null,
    val code: String? = null
)

@Serializable
data class ChatResponse(
    val choices: List<Choice> = emptyList(),
    val error: OpenAIError? = null
)

@Serializable
data class Choice(val message: ChatMessage)