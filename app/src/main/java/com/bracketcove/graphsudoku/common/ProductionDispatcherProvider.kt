package com.bracketcove.graphsudoku.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Objects are singletons, we will only have 1 ProductionProvider in memory at one time
 * thread safe
 * */
object ProductionDispatcherProvider : DispatcherProvider {
    override fun provideUIContext(): CoroutineContext {
        return Dispatchers.Main
    }

    override fun provideIOContext(): CoroutineContext {
        return Dispatchers.IO
    }
}