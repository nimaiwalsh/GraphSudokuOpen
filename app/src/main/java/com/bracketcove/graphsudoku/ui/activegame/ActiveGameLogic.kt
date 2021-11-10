package com.bracketcove.graphsudoku.ui.activegame

import com.bracketcove.graphsudoku.common.BaseLogic
import com.bracketcove.graphsudoku.common.DispatcherProvider
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.domain.IStatisticsRepository
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Represents the presentation logic (presenter). Coordinates the containers, ViewModel and the backend of the app.
 *
 * Whenever we want to run some operation against the backend, run it against a coroutine with .launch
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

    // Run the passed in action every 1 second, in this case updating the timer state in the ViewModel
    //
    // `inline` keyword: Copy/Paste to call site. No function Instance created for the lambda parameter. Improved perf.
    // `crossinline` fun type: Not allowed to add a return statement in the parameter lambda function -> Preventative
    // step to avoid situation where we might return from within the lambda causing unexpected behaviour.
    //
    // 1. Spinlock: while(true), loop that endlessly executes
    // 2. Invoke the function type
    // 3. Delays for 1 second
    //
    // To stop the endless loop (coroutine), we pass in a coroutine Job.

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ): Job = launch {
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
            return if (this <= 0) 0 else this - 1
        }

    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> onInput(
                event.input,
                viewModel.timerState
            )
            ActiveGameEvent.OnNewGameClicked -> onNewGameClicked()
            ActiveGameEvent.OnStart -> onStart()
            ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTileFocused -> onTileFocused(event.x, event.y)
        }
    }

    private fun onTileFocused(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }

    /**
     * Save users progress and shut everything down.
     * */
    private fun onStop() {
        if (!viewModel.isCompleteState) {
            launch {
                gameRepo.saveGame(
                    viewModel.timerState.timeOffset,
                    // onSuccess
                    { cancelStuff() },
                    // onError
                    {
                        cancelStuff()
                        container.showError()
                    }
                )
            }
        } else {
            cancelStuff()
        }
    }

    private fun onStart() = launch {
        gameRepo.getCurrentGame(
            // onSuccess
            { puzzle, isComplete ->
                viewModel.initializeBoardState(
                    puzzle,
                    isComplete
                )

                // Start the coroutine timer
                if (!isComplete) timerTracker = startCoroutineTimer {
                    viewModel.updateTimerState()
                }
            },
            // onError
            {
                // When game is run for the first time, an error is returned as no current game exists. Instead,
                // treat it as a new game clicked.
                container.onNewGameClick()
            }
        )
    }

    private fun onNewGameClicked() = launch {
        viewModel.showLoadingState()

        // If user hasn't completed the game yet, store the progress for the current game instead of whiping the game.
        if (!viewModel.isCompleteState) {
            gameRepo.getCurrentGame(
                // onSuccess
                { puzzle, _ ->
                    updateWithTime(puzzle)
                },
                // onError
                {
                    container.showError()
                }
            )
        } else {
            navigateToNewGame()
        }
    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(
            puzzle.copy(elapsedTime = viewModel.timerState.timeOffset),
            // onSuccess
            { navigateToNewGame() },
            // onError
            {
                container.showError()
                navigateToNewGame()
            }
        )
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container.onNewGameClick()
    }

    // Cancel all coroutines
    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTile: SudokuTile? = null
        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTile = it
        }

        if (focusedTile != null) {
            gameRepo.updateNode(
                focusedTile!!.x,
                focusedTile!!.y,
                input,
                elapsedTime,
                // success
                { isComplete ->
                    focusedTile?.let {
                        viewModel.updateBoardState(
                            it.x,
                            it.y,
                            input,
                            false
                        )
                    }

                    if (isComplete) {
                        timerTracker?.cancel()
                        checkIfNewRecord()
                    }
                },
                // failure
                { container.showError() }
            )
        }
    }

    private fun checkIfNewRecord() = launch {
        statsRepo.updateStatistics(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            // success
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            // failure,
            {
                container.showError()
                viewModel.updateCompleteState()
            }
        )
    }
}