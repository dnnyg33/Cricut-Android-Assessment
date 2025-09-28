package com.cricut.androidassessment

import com.cricut.androidassessment.model.MultipleAnswerMultipleChoiceQuestion
import com.cricut.androidassessment.model.OpenEndedQuestion
import com.cricut.androidassessment.model.Question
import com.cricut.androidassessment.model.SingleAnswerMultipleChoiceQuestion
import javax.inject.Inject

interface QuestionsRepository {
    suspend fun fetchQuestions(): List<Question>
}

class QuestionsRepositoryImpl @Inject constructor(): QuestionsRepository {

    override suspend fun fetchQuestions(): List<Question> {
        kotlinx.coroutines.delay(2000)
        return listOf(
            SingleAnswerMultipleChoiceQuestion(
                id = "q1",
                prompt = "Which Jetpack library is used for declarative UI on Android?",
                options = listOf("Views", "Jetpack Compose", "Glide", "Room"),
                correctIndex = 1
            ),
            OpenEndedQuestion(
                id = "q2",
                prompt = "In one sentence, explain what a Composable function is.",
            ),
            MultipleAnswerMultipleChoiceQuestion(
                id = "q3",
                prompt = "Select all the benefits of using Jetpack Compose:",
                options = listOf(
                    "Less boilerplate code",
                    "Easier to read and maintain UI code",
                    "Improved app performance",
                    "Requires XML layouts"
                ),
                correctIndices = setOf(0, 1, 2)
            )
        )
    }
}
