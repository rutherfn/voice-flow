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

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a voice command consisting of a list of keywords to detect.
 *
 * @property keywords The list of keywords associated with this voice command.
 */
@Parcelize
data class VoiceCommand(val keywords: List<String>) : Parcelable {

    companion object {
        /**
         * Helper method to build a [VoiceCommand] from a variable number of keywords.
         *
         * The keywords will be converted to lowercase to ensure case-insensitive matching.
         *
         * @param keywords Vararg list of keywords for the command.
         * @return A [VoiceCommand] instance with the specified keywords.
         */
        fun build(vararg keywords: String): VoiceCommand {
            return VoiceCommand(keywords.map { it.lowercase() })
        }
    }
}


