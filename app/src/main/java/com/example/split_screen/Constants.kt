package com.example.split_screen

object Constants {

    object Greeting {
        const val DEFAULT_NAME = "Android"
    }

    object Preferences {
        const val KEY_SPLIT_MODE = "pref_split_mode"
        const val KEY_APP_PACKAGE_A = "pref_app_package_a"
        const val KEY_APP_PACKAGE_B = "pref_app_package_b"
    }

    object Timeout {
        const val DEFAULT_MS = 5000L
        const val SHORT_MS = 1000L
    }

    object FreeTier {
        const val MAX_APPS = 2
        const val SESSION_MINUTES = 30
    }
}
