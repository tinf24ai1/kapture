package com.kapture.kapture.ai

// Sealed class representing the state of AI-generated project ideas
sealed class IdeaState() {

    data class Success(
        val title: String,
        val desc: String
    ) : IdeaState()

    data class Failure(
        val message: String
    ) : IdeaState()

    data object Idle : IdeaState()

    data object Loading : IdeaState()

}