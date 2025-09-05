package com.nicholas.rutherford.voice.flow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nicholas.rutherford.voice.flow.demo.DemoScreen
import com.nicholas.rutherford.voice.flow.theme.VoiceFlowTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceFlowTheme {
                DemoScreen(
                    viewModel = getViewModel()
                )
            }
        }
    }
}