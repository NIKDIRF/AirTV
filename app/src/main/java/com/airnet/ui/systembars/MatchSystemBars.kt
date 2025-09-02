package com.airnet.ui.systembars

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@SuppressLint("ContextCastToActivity")
@Composable
fun MatchSystemBars(color: Color) {
    val activity = LocalContext.current as Activity
    val view = LocalView.current
    val lightIcons = color.luminance() > 0.5f

    SideEffect {
        val window = activity.window
        window.statusBarColor = color.toArgb()
        window.navigationBarColor = color.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = lightIcons
        controller.isAppearanceLightNavigationBars = lightIcons
    }
}