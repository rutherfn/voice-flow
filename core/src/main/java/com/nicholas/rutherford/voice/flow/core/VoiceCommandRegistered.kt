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
import java.util.*

/**
 * Handles registration and listening for voice commands using Android's SpeechRecognizer API.
 *
 * This class allows developers to register custom [VoiceCommand]s, including an optional stop command
 * to halt listening automatically. It wraps [SpeechCommandListener] internally to handle the
 * lower-level speech recognition events.
 *
 * Example usage:
 * ```
 * val voiceCommand = VoiceCommand(keywords = listOf("start recording"))
 * val stopCommand = VoiceCommand(keywords = listOf("stop"))
 *
 * val voiceCommandRegistered = VoiceCommandRegistered(
 *     context = context,
 *     onCommandDetected = { command -> println("Detected: $command") },
 *     onError = { error -> Log.e("VoiceError", error) }
 * )
 *
 * voiceCommandRegistered.registerCommand(voiceCommand)
 * voiceCommandRegistered.registerStopCommand(stopCommand)
 * voiceCommandRegistered.startListening()
 * ```
 *
 * @param context Application or activity context
 * @param onCommandDetected Callback for when a registered [VoiceCommand] is recognized
 * @param onError Callback for reporting errors during speech recognition
 * @param language Optional locale to define the recognition language (default: system locale)
 */
class VoiceCommandRegistered(
    context: Context,
    onCommandDetected: (VoiceCommand) -> Unit,
    onError: (String) -> Unit,
    language: Locale = Locale.getDefault()
) {

    /**
     * Indicates whether the system is currently listening for voice commands.
     */
    var isListening: Boolean = false

    /**
     * Remembers whether the system had previously been listening before the last stop.
     */
    var hasPreviouslyListened: Boolean = false

    private val voiceCommands = mutableListOf<VoiceCommand>()
    private var startCommand: VoiceCommand? = null
    private var stopCommand: VoiceCommand? = null

    private val speechListener: SpeechCommandListener = SpeechCommandListener(
        context = context,
        onCommandDetected = onCommandDetected,
        onError = onError,
        declaredCommands = voiceCommands,
        startCommandKeywords = startCommand?.keywords.orEmpty(),
        stopCommandKeywords = stopCommand?.keywords.orEmpty(),
        onStartCommandDetected = { startListening() },
        onStopCommandDetected = { stopListening() },
        language = language
    )

    /**
     * Registers a new [VoiceCommand] that the recognizer should respond to.
     *
     * @param voiceCommand The voice command to listen for.
     */
    fun registerCommand(voiceCommand: VoiceCommand) {
        voiceCommands.add(voiceCommand)
    }

    /**
     * Registers a special [VoiceCommand] that will start listening automatically if detected.
     * This command is also added to the general command list.
     *
     * @param voiceCommand The voice command that should trigger startListening().
     */
    fun registerStartCommand(voiceCommand: VoiceCommand) {
        startCommand = voiceCommand
        voiceCommands.add(voiceCommand)
    }

    /**
     * Registers a special [VoiceCommand] that will stop listening automatically if detected.
     * This command is also added to the general command list.
     *
     * @param voiceCommand The voice command that should trigger stopListening().
     */
    fun registerStopCommand(voiceCommand: VoiceCommand) {
        stopCommand = voiceCommand
        voiceCommands.add(voiceCommand)
    }

    /**
     * Removes a previously registered [VoiceCommand] from the recognition list.
     *
     * @param voiceCommand The voice command to unregister.
     */
    fun unregisterCommand(voiceCommand: VoiceCommand) {
        voiceCommands.remove(voiceCommand)
    }

    /**
     * Starts listening for speech that matches any registered commands.
     * Safe to call multiple times; will only start if not already listening.
     */
    fun startListening() {
        hasPreviouslyListened = isListening
        if (!isListening) {
            isListening = true
            speechListener.start()
        }
    }

    /**
     * Stops listening for voice input and resets internal state.
     * Safe to call multiple times; will only stop if actively listening.
     */
    fun stopListening() {
        hasPreviouslyListened = isListening
        if (isListening) {
            isListening = false
            speechListener.stop()
        }
    }

}