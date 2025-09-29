package com.cricut.androidassessment.ai

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions
import kotlin.math.sqrt

data class EvalResult(val score: Float, val verdict: Verdict, val feedback: String)
enum class Verdict { CORRECT, PARTIAL, INCORRECT }

class AnswerEvaluator private constructor(private val embedder: TextEmbedder) {

    companion object {
        // Tune per question later if needed
        private const val TH_CORRECT = 0.82f
        private const val TH_PARTIAL = 0.70f

        fun create(appContext: Context, modelAssetPath: String = "embeddings/model.tflite"): AnswerEvaluator {
            val base = BaseOptions.builder()
                .setModelAssetPath(modelAssetPath)
                // .setNumThreads(2)
                .build()

            val options = TextEmbedderOptions.builder()
                .setBaseOptions(base)
                .setL2Normalize(true)   // cosine-friendly
                .setQuantize(true)      // if model supports it
                .build()

            val embedder = TextEmbedder.createFromOptions(appContext, options)
            return AnswerEvaluator(embedder)
        }
    }

    fun evaluate(
        question: String,
        studentAnswerRaw: String,
        referenceAnswers: List<String>,
        requiredKeywords: Set<String> = emptySet(),
        minTokens: Int = 4
    ): EvalResult {
        val student = normalize(studentAnswerRaw)
        val tokenCount = student.split(Regex("\\s+")).count { it.isNotBlank() }
        if (student.isBlank() || tokenCount < minTokens) {
            return EvalResult(0f, Verdict.INCORRECT, "Please give a fuller answer.")
        }

        val missing = requiredKeywords.filterNot { kw -> student.contains(kw, ignoreCase = true) }
        val hasAllKeywords = missing.isEmpty()

        val studentVec = embed(student)
        var best = -1f

        for (ref in referenceAnswers) {
            val s = cosine(studentVec, embed(normalize(ref)))
            if (s > best) best = s
        }

        val verdict = when {
            best >= TH_CORRECT && hasAllKeywords -> Verdict.CORRECT
            best >= TH_PARTIAL -> Verdict.PARTIAL
            else -> Verdict.INCORRECT
        }

        val feedback = when (verdict) {
            Verdict.CORRECT -> "Nice job — that matches the expected idea."
            Verdict.PARTIAL ->
                if (!hasAllKeywords && missing.isNotEmpty())
                    "Good start. Include: ${missing.joinToString(", ")}."
                else "Close. Add one more key detail to fully answer."
            Verdict.INCORRECT ->
                if (!hasAllKeywords && missing.isNotEmpty())
                    "Not quite. Be sure to mention: ${missing.joinToString(", ")}."
                else "Not quite. Re-read the question and try again with more detail."
        }

        return EvalResult(best, verdict, feedback)
    }

    private fun embed(text: String): FloatArray {
        val res = embedder.embed(text)
        // If you produce multiple embeddings per input, use the first sentence-level vector.
        return res.embeddingResult().embeddings().get(0).floatEmbedding()
    }

    private fun cosine(a: FloatArray, b: FloatArray): Float {
        var dot = 0f; var na = 0f; var nb = 0f
        for (i in a.indices) { dot += a[i]*b[i]; na += a[i]*a[i]; nb += b[i]*b[i] }
        return (dot / (sqrt(na) * sqrt(nb))).coerceIn(-1f, 1f)
    }

    private fun normalize(s: String): String =
        s.trim().lowercase()
            .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
            .replace(Regex("\\s+"), " ")
}
