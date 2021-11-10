package com.bracketcove.graphsudoku.ui.activegame.buildlogic

import android.content.Context
import com.bracketcove.graphsudoku.common.ProductionDispatcherProvider
import com.bracketcove.graphsudoku.persistence.*
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameContainer
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameLogic
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameViewModel

/**
 * No need to use Dagger, Hilts Dependency Injection for small apps, instead write the code ourself.
 * */

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {
    return ActiveGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(context.statisticsDataStore),
        ProductionDispatcherProvider
    )
}