package com.kl3jvi.chat.di

import com.kl3jvi.chat.data.ChatClient
import com.kl3jvi.chat.fragments.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ChatViewModel(get()) }
}

val clientModule = module {
    single { ChatClient() }
}
