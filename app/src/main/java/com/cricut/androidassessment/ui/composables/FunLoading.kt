package com.cricut.androidassessment.ui.composables

// ui/FunLoading.kt

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FunLoading(
    modifier: Modifier = Modifier,
    label: String = "Loading questions…"
) {
    val transition = rememberInfiniteTransition(label = "dots")
    // Animate a phase angle 0..2π repeatedly; we'll use phase offsets per dot.
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val phases = listOf(0f, 0.8f, 1.6f) // offsets per dot
    val scales = phases.map { ph ->
        // 0.75..1.15 scaling (subtle bounce)
        0.95f + 0.20f * ((sin(t + ph) + 1f) / 2f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { stateDescription = label },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            scales.forEach { s ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .scale(s)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
