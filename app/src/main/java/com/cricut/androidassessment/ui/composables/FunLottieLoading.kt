package com.cricut.androidassessment.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun FunLottieLoading(
    modifier: Modifier = Modifier,
    label: String = "Loading questions…"
) {
    // val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.party_confetti))
    // val progress by animateLottieCompositionAsState(
    //     composition,
    //     iterations = LottieConstants.IterateForever
    // )
    //
    // Column(
    //     modifier = modifier
    //         .fillMaxSize()
    //         .padding(24.dp),
    //     verticalArrangement = Arrangement.Center,
    //     horizontalAlignment = Alignment.CenterHorizontally
    // ) {
    //     LottieAnimation(
    //         composition = composition,
    //         progress = { progress },
    //         modifier = Modifier.size(180.dp)
    //     )
    //     Spacer(Modifier.height(12.dp))
    //     Text(label, style = MaterialTheme.typography.titleMedium)
    // }
}
