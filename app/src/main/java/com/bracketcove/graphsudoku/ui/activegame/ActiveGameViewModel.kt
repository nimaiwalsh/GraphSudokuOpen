package com.bracketcove.graphsudoku.ui.activegame

import com.bracketcove.graphsudoku.domain.Difficulty
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash

/**
 * We are using our own publisher/subscriber relationship pattern with the view instead of Androids JetPacks
 * We will use Kotlins function types to create a simple/crude version publisher/subscriber (observer) pattern.
 * */
class ActiveGameViewModel {
    // sub is short for subject, i.e Publisher-Subject pattern
    // Virtual representation of the board
    internal var subBoardState: ((HashMap<Int, SudokuTile>) -> Unit)? = null
    // 3 different states: 1. loading the data, 2. currently active game, 3. completed game
    internal var subContentState: ((ActiveGameScreenState) -> Unit)? = null
    // Countup timer, how long it takes for the user to complete a game
    internal var subTimerState: ((Long) -> Unit)? = null

    internal fun updateTimerState() {
        // Long value milliseconds.
        timerState++
        subTimerState?.invoke(timerState)
    }

    internal var subIsCompleteState: ((Boolean) -> Unit)? = null

    internal var timerState: Long = 0L

    internal var difficulty = Difficulty.MEDIUM
    internal var boundary = 9
    internal var boardState: HashMap<Int, SudokuTile> = HashMap()

    internal var isCompleteState: Boolean = false
    internal var isNewRecordState: Boolean = false

    // Taking the state as it exists in storage and providing it to the ViewModel and transforming it
    // into the required View's representation `SudokuTile`
    fun initializeBoardState(
        puzzle: SudokuPuzzle,
        isComplete: Boolean,
    ) {
        puzzle.graph.forEach {
            val node = it.value[0]
            boardState[it.key] = SudokuTile(
                node.x,
                node.y,
                node.color,
                hasFocus = false,
                node.readOnly
            )
        }

        val contentState: ActiveGameScreenState

        if (isComplete) {
            isCompleteState = true
            contentState = ActiveGameContentState.COMPLETE
        } else {
            contentState = ActiveGameContentState.ACTIVE
        }

        boundary = puzzle.boundary
        difficulty = puzzle.difficulty
        timerState = puzzle.elapsedTime

        subIsCompleteState?.invoke(isCompleteState)
        subContentState?.invoke(contentState)
        subBoardState?.invoke(boardState)
    }

    internal fun updateBoardState(
        x: Int,
        y: Int,
        value: Int,
        hasFocus: Boolean
    ) {
        boardState[getHash(x, y)]?.let {
            it.value = value
            it.hasFocus = hasFocus
        }

        // update individual tile
        subBoardState?.invoke(boardState)
    }

    internal fun showLoadingState() {
        subContentState?.invoke(ActiveGameScreenState.LOADING)
    }

    internal fun updateFocusState(x: Int, y: Int) {
        boardState.values.forEach {
            it.hasFocus = it.x == x && it.y == y
        }

        subBoardState?.invoke(boardState)
    }

    fun updateCompleteState() {
        isCompleteState = true
        subContentState?.invoke(ActiveGameScreenState.COMPLETE)
    }
}

/**
 * A virtual representation of a single tile in a Sudoku puzzle
 * */
class SudokuTile(
    val x: Int,
    val y: Int,
    var value: Int,
    var hasFocus: Boolean,
    val readOnly: Boolean,
)
