package com.cricut.androidassessment.model

sealed interface Question {
    val id: String
    val prompt: String
}

data class SingleAnswerMultipleChoiceQuestion(
    override val id: String,
    override val prompt: String,
    val options: List<String>,            // 4 options (single correct required)
    val correctIndex: Int,
) : Question

data class MultipleAnswerMultipleChoiceQuestion(
    override val id: String,
    override val prompt: String,
    val options: List<String>,            // 4 options (multiple correct not required)
    val correctIndices: Set<Int> = emptySet() // cannot be empty?
) : Question

data class OpenEndedQuestion(
    override val id: String,
    override val prompt: String,
    val placeholder: String = "Your answer here"
) : Question
