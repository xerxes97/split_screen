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

    object Picker {
        const val TAG_TOP = "picker_top"
        const val TAG_BOTTOM = "picker_bottom"
        const val RESULT_APP = "picker_result_app"
        const val STATE_TOP = "selection_top"
        const val STATE_BOTTOM = "selection_bottom"
    }

    object Launch {
        /** Espera entre abrir la app inferior (al lado) y la superior. */
        const val SPLIT_DELAY_MS = 1000L
    }

    object FreeTier {
        const val MAX_APPS = 2
        const val SESSION_MINUTES = 30
    }

    object ProStatus {
        const val DATASTORE_NAME = "pro_status"
        const val KEY_IS_PRO_USER = "is_pro_user"
        const val DEFAULT_IS_PRO_USER = false
    }
}
