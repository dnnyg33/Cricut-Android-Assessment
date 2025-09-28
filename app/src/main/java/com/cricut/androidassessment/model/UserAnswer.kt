package com.cricut.androidassessment.model

//renamed from Answer
sealed interface UserAnswer {
    val questionId: String

    fun canGoNext(): Boolean {
        return when (this) {
            is SingleChoiceAnswer -> true // Always can go next if single choice is answered
            is MultipleChoiceAnswer -> selectedIndices.isNotEmpty() // Must have at least one selection
            is TextAnswer -> text.isNotBlank() // Text must not be blank
        }
    }
}

data class SingleChoiceAnswer(
    override val questionId: String,
    val selectedIndex: Int
) : UserAnswer

data class MultipleChoiceAnswer(
    override val questionId: String,
    val selectedIndices: Set<Int>
) : UserAnswer

data class TextAnswer(
    override val questionId: String,
    val text: String
) : UserAnswer
