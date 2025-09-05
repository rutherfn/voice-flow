package com.nicholas.rutherford.voice.flow

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.nicholas.rutherford.voice.flow.AppModule
import com.nicholas.rutherford.voice.flow.core.VoiceLifecycleObserver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoinOnCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(VoiceLifecycleObserver)
    }

    fun startKoinOnCreate() {
        startKoin {
            androidContext(this@MyApplication)
            modules(AppModule().modules)
        }
    }
}