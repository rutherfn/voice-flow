/*
 * MIT License
 *
 * Copyright (c) 2025 Nicholas Rutherford
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nicholas.rutherford.voice.flow.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

/**
 * Listens for speech input and captures complete phrases.
 *
 * This class manages the Android [SpeechRecognizer] lifecycle and
 * captures the full transcript of recognized speech. Unlike [SpeechCommandListener],
 * this listener automatically stops after capturing one phrase and does not
 * restart listening continuously.
 *
 * @param context The Android context used to create the speech recognizer.
 * @param onPhraseDetected Callback invoked when a phrase is detected, providing the full transcript.
 * @param onError Callback invoked when an error occurs, providing an error message.
 * @param language The language locale to use for speech recognition. Defaults to device locale.
 */
class PhraseListener(
    private val context: Context,
    private val onPhraseDetected: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val language: Locale = Locale.getDefault()
) {

    private var speechRecognizer: SpeechRecognizer? = null

    /**
     * Indicates whether the phrase listener is currently active.
     */
    var isListening: Boolean = false

    /**
     * Starts the speech recognizer and begins listening for a phrase.
     *
     * If speech recognition is not available on the device, triggers the [onError] callback.
     * The listener will automatically stop after capturing one phrase.
     */
    fun start() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            val message = "Speech recognition not available. Ensure your device supports it."
            onError(message)
            Log.e("PhraseListener", message)
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val transcript = matches?.firstOrNull()

                    transcript?.let { phrase ->
                        Log.d("PhraseListener", "Phrase detected: $phrase")
                        onPhraseDetected(phrase)
                    } ?: run {
                        onError("No speech detected")
                    }

                    // Stop listening after capturing one phrase
                    stop()
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        else -> "Unknown error: $error"
                    }
                    
                    onError("SpeechRecognizer error: $errorMessage")
                    stop()
                }

                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        isListening = true
        speechRecognizer?.startListening(createIntent())
    }

    /**
     * Stops the speech recognizer and releases its resources.
     */
    fun stop() {
        isListening = false
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    /**
     * Creates an [Intent] configured for free-form speech recognition using the specified language.
     *
     * @return Configured [Intent] to start speech recognition.
     */
    private fun createIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }
}
