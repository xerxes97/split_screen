package com.example.split_screen.model

/** Reglas para habilitar el lanzamiento de pantalla dividida. */
object LaunchPolicy {

    /** true solo con ambas apps elegidas. */
    fun canLaunch(selection: SplitSelection): Boolean =
        selection.top != null && selection.bottom != null
}
