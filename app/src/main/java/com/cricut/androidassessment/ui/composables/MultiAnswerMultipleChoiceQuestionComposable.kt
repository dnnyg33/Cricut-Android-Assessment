package com.cricut.androidassessment.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.model.MultipleAnswerMultipleChoiceQuestion

@Composable
fun MultiAnswerMultipleChoiceQuestionComposable(
    question: MultipleAnswerMultipleChoiceQuestion,
    selectedIndices: Set<Int>,
    onSelect: (Int) -> Unit,
    onDeselect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = question.prompt)
        Spacer(Modifier.height(12.dp))
        question.options.forEachIndexed { index, string ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (selectedIndices.contains(index)) {
                            onDeselect(index)
                        } else {
                            onSelect(index)
                        }
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = selectedIndices.contains(index),
                    onCheckedChange = {
                        if (it) {
                            onSelect(index)
                        } else {
                            onDeselect(index)
                        }
                    }
                )
                Spacer(Modifier.width(12.dp))
                Text(text = string,  modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
}