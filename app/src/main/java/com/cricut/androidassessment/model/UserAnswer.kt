package com.cricut.androidassessment.model

//renamed from Answer
sealed interface UserAnswer {
    val questionId: String
}

data class SingleChoiceAnswer(
    override val questionId: String,
    val selectedIndex: Int
) : UserAnswer

data class TextAnswer(
    override val questionId: String,
    val text: String
) : UserAnswer
