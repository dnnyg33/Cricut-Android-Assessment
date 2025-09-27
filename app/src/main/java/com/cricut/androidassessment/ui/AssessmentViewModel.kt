package com.cricut.androidassessment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.model.OpenEndedQuestion
import com.cricut.androidassessment.model.Question
import com.cricut.androidassessment.model.SingleAnswerMultipleChoiceQuestion
import com.cricut.androidassessment.model.SingleChoiceAnswer
import com.cricut.androidassessment.model.TextAnswer
import com.cricut.androidassessment.model.UserAnswer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssessmentUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<String, UserAnswer> = emptyMap()
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isFirst: Boolean get() = currentIndex == 0
    val isLast: Boolean get() = currentIndex == questions.lastIndex
}
@HiltViewModel
class AssessmentViewModel
@Inject constructor() : ViewModel() {
    //todo replace with call to repository
    private val _uiState = MutableStateFlow(
        AssessmentUiState(
            questions = listOf(
                SingleAnswerMultipleChoiceQuestion(
                    id = "q1",
                    prompt = "Which Jetpack library is used for declarative UI on Android?",
                    options = listOf("Views", "Jetpack Compose", "Glide", "Room"),
                    correctIndex = 1
                ),
                OpenEndedQuestion(
                    id = "q2",
                    prompt = "In one sentence, explain what a Composable function is.",
                )
            )
        )
    )
    val uiState: StateFlow<AssessmentUiState> = _uiState

    fun selectOption(questionId: String, index: Int) = viewModelScope.launch {
        _uiState.update { state ->
            val updated = state.answers + (questionId to SingleChoiceAnswer(questionId, index))
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
            if (state.currentIndex < state.questions.lastIndex)
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
