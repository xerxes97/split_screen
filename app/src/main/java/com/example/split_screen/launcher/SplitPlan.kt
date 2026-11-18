package com.example.split_screen.launcher

/**
 * Orden de apertura para dividir la pantalla.
 *
 * [adjacentPkg] se abre primero con LAUNCH_ADJACENT y queda abajo.
 * [followPkg] se abre después de [delayMs] y queda arriba, en la mitad de nuestra app.
 */
data class SplitPlan(val adjacentPkg: String, val followPkg: String, val delayMs: Long)

/** Resultado de validar las apps elegidas. */
sealed class PlanResult {
    data class Ready(val plan: SplitPlan) : PlanResult()
    data class Failure(val error: LaunchError) : PlanResult()
}

enum class LaunchError {
    TOP_NOT_FOUND,
    TOP_NOT_LAUNCHABLE,
    BOTTOM_NOT_FOUND,
    BOTTOM_NOT_LAUNCHABLE
}
