package com.example.split_screen.ui

import androidx.annotation.StringRes
import com.example.split_screen.R
import com.example.split_screen.service.SplitAccessibilityService.LaunchResult.LaunchError

/** Texto para mostrar al usuario por cada error de lanzamiento. */
@StringRes
fun LaunchError.messageRes(): Int = when (this) {
    LaunchError.TOP_NOT_FOUND -> R.string.launch_error_top_not_found
    LaunchError.TOP_NOT_LAUNCHABLE -> R.string.launch_error_top_not_launchable
    LaunchError.BOTTOM_NOT_FOUND -> R.string.launch_error_bottom_not_found
    LaunchError.BOTTOM_NOT_LAUNCHABLE -> R.string.launch_error_bottom_not_launchable
    LaunchError.SERVICE_NOT_CONNECTED -> R.string.launch_error_service_not_connected
}
