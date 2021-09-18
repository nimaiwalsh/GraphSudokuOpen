package com.bracketcove.graphsudoku.common

import kotlin.coroutines.CoroutineContext

/**
 * Provide dispatcher on either the main thread (UI) or on a background thread (IO)
 * makes code easier to test
 * */
interface DispatcherProvider {
    fun provideUIContext(): CoroutineContext
    fun provideIOContext(): CoroutineContext
}