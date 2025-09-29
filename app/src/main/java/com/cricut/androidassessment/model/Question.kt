package com.cricut.androidassessment.model

sealed interface Question {
    val isScorable: Boolean
    val id: String
    val prompt: String

    val userAnswer: UserAnswer?
        get() = null

    fun selectOption(index: Int): UserAnswer
    fun deselectOption(index: Int): UserAnswer

    fun updateAnswer(newAnswer: UserAnswer): Question {
        return when (this) {
            is SingleAnswerMultipleChoiceQuestion -> this.copy(userAnswer = newAnswer as? SingleChoiceAnswer)
            is MultipleAnswerMultipleChoiceQuestion -> this.copy(userAnswer = newAnswer as? MultipleChoiceAnswer)
            is OpenEndedQuestion -> this.copy(userAnswer = newAnswer as? TextAnswer)
        }
    }
}

data class SingleAnswerMultipleChoiceQuestion(
    override val id: String,
    override val prompt: String,
    override var userAnswer: SingleChoiceAnswer? = null,
    val options: List<String>,            // 4 options (single correct required)
    val correctIndex: Int, override val isScorable: Boolean = true,
) : Question {
    override fun selectOption(index: Int): UserAnswer {
        return SingleChoiceAnswer(id, index)
    }

    override fun deselectOption(index: Int): UserAnswer {
        return SingleChoiceAnswer(id, index)
    }
}

data class MultipleAnswerMultipleChoiceQuestion(
    override val id: String,
    override val prompt: String,
    override val userAnswer: MultipleChoiceAnswer? = null,
    val options: List<String>,            // 4 options (multiple correct not required)
    val correctIndices: Set<Int> = emptySet(), // cannot be empty?
    override val isScorable: Boolean = true,
) : Question {
    override fun selectOption(index: Int): UserAnswer {
        val newSelected = (userAnswer?.selectedIndices ?: emptySet()) + index
        return MultipleChoiceAnswer(id, newSelected)
    }

    override fun deselectOption(index: Int): UserAnswer {
        val newSelected = (userAnswer?.selectedIndices ?: emptySet()) - index
        return MultipleChoiceAnswer(id, newSelected)
    }
}

data class OpenEndedQuestion(
    override val id: String,
    override val prompt: String,
    override val userAnswer: TextAnswer? = null,
    val placeholder: String = "Your answer here",
    override val isScorable: Boolean = true,
) : Question {
    override fun selectOption(index: Int): UserAnswer {
        // No-op for text answer; selection by index not applicable
        return this.userAnswer ?: TextAnswer(id, "", 0.0f)
    }

    override fun deselectOption(index: Int): UserAnswer {
        // No-op for text answer; deselection by index not applicable
        return this.userAnswer ?: TextAnswer(id, "", 0.0f)
    }
}
