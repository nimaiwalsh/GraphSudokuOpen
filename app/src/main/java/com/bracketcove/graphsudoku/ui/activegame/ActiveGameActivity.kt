package com.bracketcove.graphsudoku.ui.activegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.common.makeToast
import com.bracketcove.graphsudoku.ui.GraphSudokuTheme
import com.bracketcove.graphsudoku.ui.activegame.buildlogic.buildActiveGameLogic

/**
 * Feature specific container setup as a container for composable
 */
class ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {

    private lateinit var logic: ActiveGameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        // Set the compose view
        // Wrap component in theme
        setContent {
            GraphSudokuTheme {
                ActiveGameScreen(
                    // function types serving as our event handler, forward the onclick events in the composable to
                    // presentation logic class
                    onEventHandler = logic::onEvent, // function reference, pointing to the onEvent function of the logic class
                    viewModel
                )
            }
        }

        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)
    }

    override fun showError() = makeToast(getString(R.string.generic_error))

    override fun onNewGameClick() {
        startActivity(
            Intent(
               this,
               NewGameActivity::class.java
            )
        )
    }

}