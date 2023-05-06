package com.kl3jvi.chat.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.collect(
    flow: Flow<T>,
    collector: suspend (T) -> Unit
): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.catch { e -> e.printStackTrace() }.collect(collector)
        }
    }
}