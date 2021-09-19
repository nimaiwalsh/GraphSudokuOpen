package com.bracketcove.graphsudoku.domain

import java.io.Serializable
import java.util.*
import kotlin.collections.LinkedHashMap

// Data Models: Virtual Representations of a real world objects. In this case a SudokoPuzzle
// design the class by asking questions of what constututes a puzzle such as:
// Boundaries, Difficulty, Elapsed time, Graph

/**
 * Represents a virtual sudoku puzzle
 * Needs to extend Serializable as we will be storing this object with an ObjectOutPutStream
 * */
data class SudokuPuzzle(
    val boundary: Int,
    val difficulty: Difficulty,
    val graph: LinkedHashMap<Int, LinkedList<SudokoNode>>
    = buildNewSudoku(boundary, difficulty).graph,
    var elapsedTime: Long = 0L
): Serializable {
    fun getValue() = graph
}

