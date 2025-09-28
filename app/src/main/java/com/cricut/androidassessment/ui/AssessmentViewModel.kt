package com.cricut.androidassessment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.QuestionsRepository
import com.cricut.androidassessment.model.MultipleChoiceAnswer
import com.cricut.androidassessment.model.Question
import com.cricut.androidassessment.model.SingleChoiceAnswer
import com.cricut.androidassessment.model.TextAnswer
import com.cricut.androidassessment.model.UserAnswer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssessmentUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<String, UserAnswer> = emptyMap(),
    val isLoading: Boolean = false,
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isFirst: Boolean get() = currentIndex == 0
    val isLast: Boolean get() = currentIndex == questions.lastIndex
}
@HiltViewModel
class AssessmentViewModel
@Inject constructor(private val questionsRepository: QuestionsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AssessmentUiState(
            )
    )

    fun load() = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }
        runCatching { questionsRepository.fetchQuestions() }
            .onSuccess { questions ->
                _uiState.update {
                    it.copy(questions = questions, isLoading = false)
                }
            }
            .onFailure { throwable ->
                _uiState.update {
                    it.copy(isLoading = false)
                }
                // Handle error, e.g., log or show a message
            }
    }
    val uiState: StateFlow<AssessmentUiState> = _uiState

    fun selectOption(questionId: String, index: Int) = viewModelScope.launch {
        _uiState.update { state ->
            val updated = state.answers + (questionId to SingleChoiceAnswer(questionId, index))
            state.copy(answers = updated)
        }
    }

    fun deselectOption(questionId: String, index: Int) = viewModelScope.launch {
        _uiState.update { state ->
            val newSelectedIndices =
                (state.answers[questionId] as? MultipleChoiceAnswer)?.selectedIndices?.toMutableSet()
                    ?: return@update state
            newSelectedIndices.remove(index)
            val updated = if (newSelectedIndices.isEmpty()) {
                state.answers.toMutableMap().apply {
                    remove(questionId)
                }
            } else {
                state.answers + (questionId to MultipleChoiceAnswer(questionId, newSelectedIndices))
            }
            state.copy(answers = updated)
        }
    }

    fun updateText(questionId: String, text: String) = viewModelScope.launch {
        _uiState.update { state ->
            val updated = state.answers + (questionId to TextAnswer(questionId, text))
            state.copy(answers = updated)
        }
    }

    fun next() = viewModelScope.launch {
        _uiState.update { state ->
            if (state.currentIndex <= state.questions.lastIndex)
                state.copy(currentIndex = state.currentIndex + 1)
            else state
        }
    }

    fun back() = viewModelScope.launch {
        _uiState.update { state ->
            if (state.currentIndex > 0)
                state.copy(currentIndex = state.currentIndex - 1)
            else state
        }
    }
}
