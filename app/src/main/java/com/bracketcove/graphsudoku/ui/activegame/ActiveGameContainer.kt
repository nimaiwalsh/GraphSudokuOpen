package com.bracketcove.graphsudoku.ui.activegame

/**
 * A container within which the various parts of an application,
 * or a specific feature of an application, are deployed with.
 *
 * This container is the main `Activity` that contains the app.
 * In a larger App, I'd suggest using Fragments as Containers; didn't make sense to with this app.
 *
 * Contains a large portion of an application, doesn't really handle business logic,
 * it wires up all the different parts of the app, serves as an entry point.
 */
interface ActiveGameContainer {
    fun showError()
    fun onNewGameClick()
}