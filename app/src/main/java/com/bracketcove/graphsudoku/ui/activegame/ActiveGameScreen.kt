package com.bracketcove.graphsudoku.ui.activegame

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.ui.components.AppToolbar
import com.bracketcove.graphsudoku.ui.textColorDark
import com.bracketcove.graphsudoku.ui.textColorLight

enum class ActiveGameScreenState {
    LOADING,
    ACTIVE,
    COMPLETE
}

/**
 * Represents the root Composable in this hierarchy of composable
 * */
@Composable
fun ActiveGameScreen(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    // Whenever we have data or state that may change at runtime, we want to wrap that data in a `remember` delegate.
    // This tells the Compose library under the hood to watch for changes and to re-draw the UI if the data changes
    val contentTransitionState = remember {
        MutableTransitionState(
            ActiveGameScreenState.LOADING
        )
    }

    // The `remember` delegate above prepares compose for updates, but we need a way to update the value types.
    // We do this by binding a lambda to one of our `function types` our ViewModel possesses.
    // When one of the functions is invoked in the ViewModel the program automatically jumps to and executes this
    // code within our composable, which in turn triggers the recomposition.
    viewModel.subContentState = {
        contentTransitionState.targetState = it
    }

    val transition = updateTransition(transitionState = contentTransitionState, label = "")

    val loadingAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }, label = ""
    ) {
        if (it == ActiveGameScreenState.LOADING) 1f else 0f
    }

    val activeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }, label = ""
    ) {
        if (it == ActiveGameScreenState.ACTIVE) 1f else 0f
    }

    val completeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }, label = ""
    ) {
        if (it == ActiveGameScreenState.COMPLETE) 1f else 0f
    }

    Column(
        Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxHeight()
    ) {
        AppToolbar(modifier = Modifier.wrapContentHeight(), title = stringResource(R.string.app_name)) {
            NewGameIcon(onEventHandler = onEventHandler)
        }

    }

}

@Composable
fun NewGameIcon(onEventHandler: (ActiveGameEvent) -> Unit) {
    Icon(
        imageVector = Icons.Filled.Add,
        contentDescription = null,
        tint = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
        modifier = Modifier
            .clickable { onEventHandler.invoke(ActiveGameEvent.OnNewGameClicked) }
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(36.dp)
    )
}
