package com.nicholas.rutherford.voice.flow

import com.nicholas.rutherford.voice.flow.demo.DemoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AppModule {

    private val defaultCoroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val modules = module {
        viewModel {
            DemoViewModel(
                application = androidApplication()
            )
        }
    }
}