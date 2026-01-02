package com.kapture.kapture.ai

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