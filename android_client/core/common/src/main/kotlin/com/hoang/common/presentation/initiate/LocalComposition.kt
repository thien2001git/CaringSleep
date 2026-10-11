package com.hoang.common.presentation.initiate

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalNavController =
    staticCompositionLocalOf<NavController> { "LocalNavController".noLocalProvidedFor() }

private fun String.noLocalProvidedFor(): Nothing {
    error("CompositionLocal $this not present")
}
