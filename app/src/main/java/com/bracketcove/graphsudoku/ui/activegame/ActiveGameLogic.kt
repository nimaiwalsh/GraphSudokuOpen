package com.bracketcove.graphsudoku.ui.activegame

import com.bracketcove.graphsudoku.common.BaseLogic
import com.bracketcove.graphsudoku.common.DispatcherProvider
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.domain.IStatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Represents the presentation logic. Coordinates the containers, ViewModel and the backend of the app.
 *
 * Coroutine Scope (or Dagger scope) -> About a lifecycle.
 * Why not use the default Android lifecycles class on Fragments/ActivitiesViewModels?
 * 1. Don't like tight coupling to the Android Platform if it can be avoided.
 * 2. This class contains presentation logic, it's the head decision maker, therefore
 *    Make it responsible for cancelling any coroutines currently running
 * */
class ActiveGameLogic(
    // container is the Activity the houses the app.
    private val container: ActiveGameContainer,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider,
) : BaseLogic<ActiveGameEvent>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext().plus(jobTracker)

    init {
        jobTracker = Job()
    }

    // `inline` keyword: Copy/Paste to call site. No function Instance created for the lambda parameter. Improved perf.
    // `crossinline` fun type: Not allowed to add a return statement in the parameter lambda funtion -> Preventative
    // step to avoid situation where we might return from within the lambda causing unexpected behaviour.
    //
    // 1. Spinlock: while(true), loop that endlessly executes
    // 2. Invoke the function type
    // 3. Delays for 1 second
    //
    // To stop the endless loop (coroutine), we pass in a coroutine Job.

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    // Add to startCoroutineTimer allowing us to cancel it.
    private var timerTracker: Job? = null

    // Make the timer less janky, causing a consistent timer.
    private val Long.timeOffset: Long
        get() {
            return if (this <= 0) 0 else this -1
        }

    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> TODO()
            ActiveGameEvent.OnNewGameClicked -> TODO()
            ActiveGameEvent.OnStart -> TODO()
            ActiveGameEvent.OnStop -> TODO()
            is ActiveGameEvent.OnTileFocused -> TODO()
        }
    }
}