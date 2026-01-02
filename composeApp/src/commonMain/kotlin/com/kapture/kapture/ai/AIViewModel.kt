package com.kapture.kapture.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AIViewModel(private val aiService: AIService) : ViewModel() {

    private val _uiState = MutableStateFlow<IdeaState>(IdeaState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onGenerateClicked() {

        viewModelScope.launch {
            _uiState.value = IdeaState.Loading

            try {
                val result = aiService.getSuggestion()

                _uiState.value = IdeaState.Success(result.split(";", limit = 2)[0], result.split(";", limit = 2)[1])
            } catch (e: Exception) {
                _uiState.value = IdeaState.Failure(e.message ?: "Unknown Error")
            }
        }
    }
}