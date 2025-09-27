import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.model.SingleAnswerMultipleChoiceQuestion

@Composable
fun SingleAnswerMultipleChoiceQuestionComposable(
    question: SingleAnswerMultipleChoiceQuestion,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = question.prompt)
        Spacer(Modifier.height(12.dp))
        question.options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                RadioButton(
                    selected = selectedIndex == index,
                    onClick = { onSelect(index) }
                )
                Spacer(Modifier.width(12.dp))
                Text(text = option)
            }
        }
    }
}
