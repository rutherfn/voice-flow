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
 * Captures complete phrases from speech input using Android's SpeechRecognizer API.
 *
 * This class provides a simple API for capturing full speech transcripts. Unlike [VoiceCommandRegistered],
 * this class is designed specifically for phrase capture and automatically stops after capturing one phrase.
 * It's perfect for use cases like voice notes, dictation, or any scenario where you need the complete
 * spoken text rather than command matching.
 *
 * Example usage:
 * ```
 * val voicePhraseCapture = VoicePhraseCapture(
 *     context = context,
 *     onPhraseCaptured = { phrase -> println("Captured: $phrase") },
 *     onError = { error -> Log.e("PhraseError", error) }
 * )
 *
 * // Start listening for a phrase
 * voicePhraseCapture.startListening()
 * 
 * // The listener will automatically stop after capturing one phrase
 * // and call onPhraseCaptured with the result
 * ```
 *
 * @param context Application or activity context
 * @param onPhraseCaptured Callback for when a phrase is captured, providing the full transcript
 * @param onError Callback for reporting errors during speech recognition
 * @param language Optional locale to define the recognition language (default: system locale)
 */
class VoicePhraseCapture(
    private val context: Context,
    private val onPhraseCaptured: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val language: Locale = Locale.getDefault()
) {

    private val phraseListener: PhraseListener = PhraseListener(
        context = context,
        onPhraseDetected = onPhraseCaptured,
        onError = onError,
        language = language
    )

    /**
     * Starts listening for a phrase. The listener will automatically stop after capturing one phrase.
     *
     * If speech recognition is not available on the device, triggers the [onError] callback.
     */
    fun startListening() = phraseListener.start()

    /**
     * Stops listening for phrases and releases resources.
     * Safe to call even if not currently listening.
     */
    fun stopListening() = phraseListener.stop()
}
