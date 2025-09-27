package com.cricut.androidassessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cricut.androidassessment.ui.screens.AssessmentScreen
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAssessmentTheme {
                AssessmentScreen()
                }
            }
        }
    }
