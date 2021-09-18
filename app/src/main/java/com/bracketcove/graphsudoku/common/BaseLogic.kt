package com.bracketcove.graphsudoku.common

import kotlinx.coroutines.Job

/**
 * Use abstract class where we want to share behaviour, share variables
 * This will be inherited by classes in the ui package
 * This class will handle events from the user interface
 */
abstract class BaseLogic<EVENT> {
    protected lateinit var jobTracker: Job
    abstract fun onEvent(event: EVENT)
}