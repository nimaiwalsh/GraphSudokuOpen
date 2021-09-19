package com.bracketcove.graphsudoku.domain

import java.lang.Exception

interface IGameDataStorage {
    suspend fun updateGame(game: SudokuPuzzle): GameStorageResult
    suspend fun updateNode(x: Int, y: Int, color: Int, elapsedTime: Long): GameStorageResult
    suspend fun getCurrentGame(): GameStorageResult
}

// Sealed class allows us to create a restricted set of types, these types can contain particular
// sets of values
sealed class GameStorageResult {
    data class OnSuccess(val currentGame: SudokuPuzzle) : GameStorageResult()
    data class OnError(val exception: Exception) : GameStorageResult()
}