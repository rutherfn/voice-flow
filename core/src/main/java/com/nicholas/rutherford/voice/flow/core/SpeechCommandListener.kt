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
 * Listens for speech input and detects specified voice commands.
 *
 * This class manages the Android [SpeechRecognizer] lifecycle and
 * matches recognized speech against a list of declared commands.
 * It also supports a dedicated stop command to trigger a stop callback.
 *
 * @param context The Android context used to create the speech recognizer.
 * @param onCommandDetected Callback invoked when a matching [VoiceCommand] is detected.
 * @param onError Callback invoked when an error occurs, providing an error message.
 * @param declaredCommands The list of voice commands to detect.
 * @param startCommandKeywords List of keywords that trigger the start command callback.
 * @param stopCommandKeywords List of keywords that trigger the stop command callback.
 * @param onStartCommandDetected Optional callback invoked when a start command is detected.
 * @param onStopCommandDetected Optional callback invoked when a stop command is detected.
 * @param language The language locale to use for speech recognition. Defaults to device locale.
 */
class SpeechCommandListener(
    private val context: Context,
    private val onCommandDetected: (VoiceCommand) -> Unit,
    private val onError: (String) -> Unit,
    private val declaredCommands: List<VoiceCommand>,
    private val startCommandKeywords: List<String> = emptyList(),
    private val stopCommandKeywords: List<String> = emptyList(),
    private val onStartCommandDetected: (() -> Unit)? = null,
    private val onStopCommandDetected: (() -> Unit)? = null,
    private val language: Locale = Locale.getDefault()
) {

    private var speechRecognizer: SpeechRecognizer? = null

    /**
     * Starts the speech recognizer and begins listening for voice commands.
     *
     * If speech recognition is not available on the device, triggers the [onError] callback.
     */
    fun start() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            val message = "Speech recognition not available. Ensure your device supports it."
            onError(message)
            Log.e("SpeechCommandListener", message)
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val transcript = matches?.firstOrNull()?.lowercase(Locale.getDefault())

                    transcript?.let { spoken ->
                        // Check each declared command for keyword matches
                        declaredCommands.forEach { command ->
                            if (command.keywords.any { spoken.contains(it) }) {
                                onCommandDetected(command)
                            }
                        }

                        // Check for start command keywords
                        if (startCommandKeywords.any { spoken.contains(it) }) {
                            onStartCommandDetected?.invoke()
                            return
                        }

                        // Check for stop command keywords
                        if (stopCommandKeywords.any { spoken.contains(it) }) {
                            onStopCommandDetected?.invoke()
                            return
                        }
                    }

                    // Restart listening after processing results
                    restartListening()
                }

                override fun onError(error: Int) {
                    onError("SpeechRecognizer error: $error")
                    restartListening()
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

        speechRecognizer?.startListening(createIntent())
    }

    /**
     * Stops the speech recognizer and releases its resources.
     */
    fun stop() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    /**
     * Cancels and restarts listening to keep the recognition active continuously.
     */
    private fun restartListening() {
        speechRecognizer?.cancel()
        speechRecognizer?.startListening(createIntent())
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
        }
    }
}