package com.example.split_screen.model

/** Reglas para habilitar el lanzamiento de pantalla dividida. */
object LaunchPolicy {

    /** true solo con ambas apps elegidas y el servicio de accesibilidad activo. */
    fun canLaunch(selection: SplitSelection, serviceActive: Boolean): Boolean =
        serviceActive && selection.top != null && selection.bottom != null
}
