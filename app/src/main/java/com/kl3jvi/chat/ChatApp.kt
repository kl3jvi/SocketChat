package com.kl3jvi.chat

import android.app.Application
import com.kl3jvi.chat.di.clientModule
import com.kl3jvi.chat.di.viewModelModule
import org.koin.core.context.GlobalContext.startKoin

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(viewModelModule, clientModule)
        }
    }
}
