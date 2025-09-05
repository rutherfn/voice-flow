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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * [VoiceLifecycleObserver] is a singleton that tracks whether the app is in the foreground or background
 * using the Android lifecycle callbacks provided by [ProcessLifecycleOwner].
 *
 * This can be useful for voice-based features that should only run while the app is actively being used.
 *
 * Developers can register listeners to be notified when the foreground state changes.
 *
 * Example:
 * ```
 * VoiceLifecycleObserver.registerListener { isForeground ->
 *     if (isForeground) {
 *         // Resume voice recognition
 *     } else {
 *         // Pause or stop voice recognition
 *     }
 * }
 *
 * VoiceLifecycleObserver.startObserving()
 * ```
 */
object VoiceLifecycleObserver : DefaultLifecycleObserver {

    private var isInForeground = false
    private val listeners = mutableListOf<(Boolean) -> Unit>()

    /**
     * Registers a listener that is notified when the app enters or exits the foreground.
     *
     * @param listener A lambda that receives `true` when the app enters the foreground,
     * and `false` when it enters the background.
     */
    fun registerListener(listener: (Boolean) -> Unit) {
        listeners.add(listener)
    }

    /**
     * Unregisters a previously registered listener.
     *
     * @param listener The lambda that was originally passed to [registerListener].
     */
    fun unregisterListener(listener: (Boolean) -> Unit) {
        listeners.remove(listener)
    }

    /**
     * Starts observing the app's foreground/background state.
     * This must be called before any lifecycle events will trigger listener callbacks.
     */
    fun startObserving() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Stops observing the app's lifecycle events.
     * Call this if you want to manually manage when foreground state changes are tracked.
     */
    fun stopObserving() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    /**
     * Called when the app moves to the foreground. Notifies all registered listeners.
     *
     * @param owner The lifecycle owner associated with this lifecycle event.
     */
    override fun onStart(owner: LifecycleOwner) {
        isInForeground = true
        listeners.forEach { it(true) }
    }

    /**
     * Called when the app moves to the background. Notifies all registered listeners.
     *
     * @param owner The lifecycle owner associated with this lifecycle event.
     */
    override fun onStop(owner: LifecycleOwner) {
        isInForeground = false
        listeners.forEach { it(false) }
    }
}

