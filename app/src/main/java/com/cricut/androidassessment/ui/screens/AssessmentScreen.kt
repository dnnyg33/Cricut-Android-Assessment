package com.cricut.androidassessment.ui.screens

import SingleAnswerMultipleChoiceQuestionComposable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.ai.AnswerEvaluator
import com.cricut.androidassessment.model.MultipleAnswerMultipleChoiceQuestion
import com.cricut.androidassessment.model.MultipleChoiceAnswer
import com.cricut.androidassessment.model.OpenEndedQuestion
import com.cricut.androidassessment.model.SingleAnswerMultipleChoiceQuestion
import com.cricut.androidassessment.model.SingleChoiceAnswer
import com.cricut.androidassessment.model.TextAnswer
import com.cricut.androidassessment.ui.AssessmentViewModel
import com.cricut.androidassessment.ui.composables.FunLoading
import com.cricut.androidassessment.ui.composables.MultiAnswerMultipleChoiceQuestionComposable
import com.cricut.androidassessment.ui.composables.OpenEndedQuestionComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    viewModel: AssessmentViewModel = viewModel(),
    evaluator: AnswerEvaluator
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    viewModel.load()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Android Quiz") }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    TextButton(
                        onClick = viewModel::back,
                        enabled = !uiState.isFirst
                    ) { Text("Back") }
                },
                floatingActionButton = {
                    val canGoNext = uiState.currentQuestion?.userAnswer?.canGoNext() == true
                    AnimatedVisibility(visible = canGoNext) {
                        ExtendedFloatingActionButton(
                            onClick = viewModel::next,
                            text = {
                                Text(
                                    if (uiState.isLast) "Finish" else "Next",
                                    textAlign = TextAlign.Center
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.isLast) Icons.Default.Check else Icons.AutoMirrored.Default.ArrowForward,
                                    contentDescription = null
                                )
                            },
                            expanded = true,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            )
        }) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                // Loading state
                FunLoading()
                return@Surface
            }
            val q = uiState.currentQuestion
            if (q == null) {
                // End uiState: simple summary (optional)

                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Quiz complete. Thanks!",
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    Text(
                        "You answered ${uiState.correctAnswersCount} questions correctly. Open-ended questions are not scored.",
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = viewModel::restartQuiz) {
                            Text("Restart Quiz")
                        }
                    }
                }
                return@Surface
            }

            when (q) {
                is SingleAnswerMultipleChoiceQuestion -> {
                    val selected =
                        (uiState.currentQuestion?.userAnswer as? SingleChoiceAnswer)?.selectedIndex
                    SingleAnswerMultipleChoiceQuestionComposable(
                        question = q,
                        selectedIndex = selected,
                        onSelect = { viewModel.selectOption(q.id, it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is OpenEndedQuestion -> {
                    val text = (uiState.currentQuestion?.userAnswer as? TextAnswer)?.text.orEmpty()
                    OpenEndedQuestionComposable(
                        question = q,
                        text = text,
                        onTextChange = { viewModel.updateText(q.id, it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is MultipleAnswerMultipleChoiceQuestion -> {
                    val selected =
                        (uiState.currentQuestion?.userAnswer as? MultipleChoiceAnswer)?.selectedIndices
                        ?: emptySet()
                    MultiAnswerMultipleChoiceQuestionComposable(
                        question = q,
                        selectedIndices = selected,
                        onSelect = { viewModel.selectOption(q.id, it) },
                        onDeselect = { viewModel.deselectOption(q.id, it) },
                    )
                }
            }
        }
    }
}
