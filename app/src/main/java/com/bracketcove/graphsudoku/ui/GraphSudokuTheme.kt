package com.bracketcove.graphsudoku.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


/**
 * Colors defined here will automatically be inherited by widgets.
 * This is especially useful when supporting light and dark themes.
 * */
private val LightColorPalette = lightColors(
    primary = primaryGreen,
    primaryVariant = gridLineColorLight,
    secondary = textColorLight,
    surface = lightGrey,
    onPrimary = accentAmber,
    onSurface = accentAmber
)

private val DarkColorPalette = darkColors(
    primary = primaryCharcoal,
    primaryVariant = gridLineColorLight,
    secondary = textColorDark,
    surface = lightGreyAlpha,
    onPrimary = accentAmber,
    onSurface = accentAmber
)

@Composable
fun GraphSudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}