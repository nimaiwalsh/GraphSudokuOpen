package com.bracketcove.graphsudoku.ui.activegame

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.ui.*
import com.bracketcove.graphsudoku.ui.components.AppToolbar
import com.bracketcove.graphsudoku.ui.components.LoadingScreen

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
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxHeight()
    ) {
        AppToolbar(modifier = Modifier.wrapContentHeight(), title = stringResource(R.string.app_name)) {
            NewGameIcon(onEventHandler = onEventHandler)
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Runs each time a recomposition occurs
            when (contentTransitionState.currentState) {
                ActiveGameScreenState.ACTIVE -> Box(
                    Modifier.alpha(activeAlpha)
                ) {
                    GameContent(
                        onEventHandler,
                        viewModel
                    )
                }
                ActiveGameScreenState.COMPLETE -> Box(
                    Modifier.alpha(completeAlpha)
                ) {
                    GameCompleteContent(
                        viewModel.timerState,
                        viewModel.isNewRecordState
                    )
                }
                ActiveGameScreenState.LOADING -> Box(
                    Modifier.alpha(loadingAlpha)
                ) {
                    LoadingScreen()
                }
            }
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

/**
 * Most complex part of our UI.
 * 9x9 puzzle has 81 different text coimposables whioch is a large number of widgets.
 * Each part of the suduko game treated as a seperate layer/element
 * AVOID writing god composables using helper functions breaking them down into smaller parts
 * */
@Composable
fun GameContent(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    // Kind of like a Composable wrapper which gives us information about the height, width and other measurements
    // which can be used inside the lambda expression
    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) {
            constraints.minWidth.toDp()
        }

        val margin = with(LocalDensity.current) {
            when {
                constraints.maxHeight.toDp().value < 500 -> 20
                constraints.maxHeight.toDp().value < 550 -> 8
                else -> 0
            }
        }

        ConstraintLayout {
            // In order to constrain composables to each other we need a way for them to reference each other
            // This is the equivalent of using ID's in XML
            val (board, timer, diff, inputs) = createRefs()

            Box(
                Modifier
                    .constrainAs(board) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(MaterialTheme.colors.surface)
                    .size(screenWidth - margin.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.primaryVariant
                    )
            ) {
                SudokuBoard(
                    onEventHandler,
                    viewModel,
                    screenWidth - margin.dp
                )
            }
        }
    }
}

/**
 * The actual SudokuBoard
 * */
@Composable
fun SudokuBoard(onEventHandler: (ActiveGameEvent) -> Unit, viewModel: ActiveGameViewModel, size: Dp) {

    // The size of the puzzle, wither 4x4, 9x9 etc.
    val boundary = viewModel.boundary

    // Used to evenly distribute the screen real-estate for each tile and gridline
    val tileOffset = size.value / boundary

    // MutableState
    var boardState by remember {
        // Setting MutableState.value will always be considered a change.
        mutableStateOf(viewModel.boardState, neverEqualPolicy())
    }

    // Whenever subBoardState is updated, it updates the boardState in the view
    viewModel.subBoardState = {
        boardState = it
    }

    SudokuTextFields(
        onEventHandler,
        tileOffset,
        boardState
    )

    BoardGrid(
        boundary,
        tileOffset
    )
}

@Composable
fun BoardGrid(boundary: Int, tileOffset: Float) {
    TODO("Not yet implemented")
}

/**
 * Tiles in the puzzle
 * Can either be read only or mutable
 * */
@Composable
fun SudokuTextFields(
    onEventHandler: (ActiveGameEvent) -> Unit,
    tileOffset: Float,
    boardState: HashMap<Int, SudokuTile>
) {
    boardState.values.forEach { tile ->
        var text = tile.value.toString()

        if (!tile.readOnly) {
            if (text == "0") text = ""

            Text(
                text = text,
                style = mutableSudokuSquare(tileOffset).copy(),
                color = if (MaterialTheme.colors.isLight) userInputtedNumberLight else userInputtedNumberDark,
                modifier = Modifier
                    // Event position each tile
                    .absoluteOffset(
                        (tileOffset * (tile.x - 1)).dp,
                        (tileOffset * (tile.y - 1)).dp
                    )
                    .size(tileOffset.dp)
                    .background(
                        if (tile.hasFocus) MaterialTheme.colors.onPrimary.copy(alpha = .25f)
                        else MaterialTheme.colors.surface
                    )
                    .clickable {
                        onEventHandler.invoke(
                            ActiveGameEvent.OnTileFocused(tile.x, tile.y)
                        )
                    }
            )
        } else {
            Text(
                text = text,
                style = readOnlySudokuSquare(tileOffset),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOffset * (tile.x - 1)).dp,
                        (tileOffset * (tile.y - 1)).dp
                    )
                    .size(tileOffset.dp)
            )
        }
    }
}
