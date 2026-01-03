package com.kapture.kapture.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel to manage AI-generated project idea state
class AIViewModel(private val aiService: AIService) : ViewModel() {

    private val _uiState = MutableStateFlow<IdeaState>(IdeaState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onGenerateClicked(title: String, desc: String) {

        viewModelScope.launch {
            _uiState.value = IdeaState.Loading

            try {
                val response = aiService.getSuggestion(title, desc)

                if (response.error != null) {
                    throw Exception("Gemini Error: ${response.error.message}")
                }

                val suggestion = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (suggestion.isNullOrBlank()) {
                    throw Exception("No response from AI: The choices list was empty.")
                }

                val result = suggestion.split(";", limit = 2).map { x: String -> x.trim() }

                _uiState.update { IdeaState.Success(result[0], result[1]) }
            } catch (e: Exception) {
                _uiState.update { IdeaState.Failure(e.message ?: "Unknown Error") }
            }
        }
    }
}