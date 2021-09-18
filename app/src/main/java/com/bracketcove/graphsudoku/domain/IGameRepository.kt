package com.bracketcove.graphsudoku.domain

import java.lang.Exception

// Interface: Very similar to a class, however you are not allowed to
// implement or give a body to the functions.
// You only give abstract functions, or function stubs.

interface IGameRepository {
    // Functions will  be called from Coroutine scopes, so we use suspend
    suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun getSettings(
        onSuccess: (Settings) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateSettings(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    )
}