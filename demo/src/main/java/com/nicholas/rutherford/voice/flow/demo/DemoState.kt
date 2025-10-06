package com.nicholas.rutherford.voice.flow.demo

data class DemoState(
    val isListening: Boolean = false,
    val isPhraseListening: Boolean = false,
    val capturedPhrase: String? = null
)