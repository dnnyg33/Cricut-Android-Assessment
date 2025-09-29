package com.cricut.androidassessment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.QuestionsRepository
import com.cricut.androidassessment.model.Question
import com.cricut.androidassessment.model.TextAnswer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssessmentUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
) {
    val correctAnswersCount: String get() {
        val correctCount = questions.count { question ->
            when (question) {
                is com.cricut.androidassessment.model.SingleAnswerMultipleChoiceQuestion -> {
                    question.userAnswer?.selectedIndex == question.correctIndex
                }
                is com.cricut.androidassessment.model.MultipleAnswerMultipleChoiceQuestion -> {
                    question.userAnswer?.selectedIndices == question.correctIndices
                }
                is com.cricut.androidassessment.model.OpenEndedQuestion -> {
                    // Open-ended questions are not scored
                    false
                }
            }
        }
        return "$correctCount out of ${questions.filter { it.isScorable }.size}"
    }
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

    fun restartQuiz() = viewModelScope.launch {
        _uiState.update {
            it.copy(currentIndex = 0, questions = emptyList())
        }
        load()
    }
    val uiState: StateFlow<AssessmentUiState> = _uiState

    fun selectOption(questionId: String, index: Int) = viewModelScope.launch {
        _uiState.update { state ->
            val updatedAnswer = state.currentQuestion?.selectOption(index)
            val questionIndex = state.questions.indexOfFirst { it.id == questionId }

            state.copy(
                questions = state.questions.toMutableList().apply {
                    if (questionIndex != -1 && updatedAnswer != null) {
                        this[questionIndex] = this[questionIndex].updateAnswer(updatedAnswer)
                    // } else {
                    //     this[questionIndex].updateAnswer()
                    }
                }
            )
        }
    }

    fun deselectOption(questionId: String, index: Int) = viewModelScope.launch {
        _uiState.update { state ->
            val newSelectedIndices =
                state.currentQuestion?.deselectOption(index)
            val questionIndex = state.questions.indexOfFirst { it.id == questionId }

            state.copy(questions = state.questions.toMutableList().apply {
                if (questionIndex != -1 && newSelectedIndices != null) {
                    this[questionIndex] = this[questionIndex].updateAnswer(newSelectedIndices)
                }
            })
        }
    }

    fun updateText(questionId: String, text: String) = viewModelScope.launch {
        _uiState.update { state ->
            val updatedAnswer = TextAnswer(questionId, text)
            val questionIndex = state.questions.indexOfFirst { it.id == questionId }
            state.copy(questions = state.questions.toMutableList().apply {
                if (questionIndex != -1) {
                    this[questionIndex] = this[questionIndex].updateAnswer(updatedAnswer)
                }
            })
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
