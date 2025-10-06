package com.nicholas.rutherford.voice.flow.demo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.nicholas.rutherford.voice.flow.core.VoiceCommand
import com.nicholas.rutherford.voice.flow.core.VoiceCommandRegistered
import com.nicholas.rutherford.voice.flow.core.VoicePhraseCapture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class DemoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val demoMutableStateFlow = MutableStateFlow(value = DemoState())
    val demoStateFlow = demoMutableStateFlow.asStateFlow()

    private val voiceCommandRegistered = VoiceCommandRegistered(
        context = application,
        onCommandDetected = { command ->
            if (command.keywords.contains("stop")) {
                stopListening()
            }
            Log.d("Command", "Detected command with: ${command.keywords}")
        },
        onError = { error ->
            Log.e("Command", error)
        },
        language = Locale.ENGLISH
    )

    private val voicePhraseCapture = VoicePhraseCapture(
        context = application,
        onPhraseCaptured = { phrase ->
            Log.d("Phrase", "Captured phrase: $phrase")
            demoMutableStateFlow.update { state -> 
                state.copy(
                    capturedPhrase = phrase,
                    isPhraseListening = false
                ) 
            }
        },
        onError = { error ->
            Log.e("Phrase", error)
            demoMutableStateFlow.update { state -> 
                state.copy(isPhraseListening = false) 
            }
        },
        language = Locale.ENGLISH
    )

    init {
        voiceCommandRegistered.registerStartCommand(VoiceCommand.build("start", "commencer", "iniciar"))
        voiceCommandRegistered.registerStopCommand(VoiceCommand.build("stop", "arrêter", "detener"))
    }

    fun startListening() {
        demoMutableStateFlow.update { state ->  state.copy(isListening = true) }
        voiceCommandRegistered.startListening()
    }

    fun stopListening() {
        voiceCommandRegistered.stopListening()
        demoMutableStateFlow.update { state ->  state.copy(isListening = false) }
    }

    fun onPause() {
        voiceCommandRegistered.stopListening()
        demoMutableStateFlow.update { state ->  state.copy(isListening = false) }
    }

    fun onResume() {
        if (voiceCommandRegistered.hasPreviouslyListened) {
            demoMutableStateFlow.update { state ->  state.copy(isListening = true) }
            voiceCommandRegistered.startListening()
        }
    }

    fun startPhraseListening() {
        demoMutableStateFlow.update { state -> 
            state.copy(
                isPhraseListening = true,
                capturedPhrase = null
            ) 
        }
        voicePhraseCapture.startListening()
    }

    fun stopPhraseListening() {
        voicePhraseCapture.stopListening()
        demoMutableStateFlow.update { state -> 
            state.copy(isPhraseListening = false) 
        }
    }
}