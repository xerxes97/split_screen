package com.example.split_screen.launcher

import com.example.split_screen.Constants

/** Valida las apps y decide el orden de apertura. Sin dependencias de Android. */
object SplitPlanner {

    fun plan(
        topPkg: String,
        bottomPkg: String,
        isInstalled: (String) -> Boolean,
        isLaunchable: (String) -> Boolean,
        delayMs: Long = Constants.Launch.SPLIT_DELAY_MS
    ): PlanResult {
        if (!isInstalled(topPkg)) return PlanResult.Failure(LaunchError.TOP_NOT_FOUND)
        if (!isLaunchable(topPkg)) return PlanResult.Failure(LaunchError.TOP_NOT_LAUNCHABLE)
        if (!isInstalled(bottomPkg)) return PlanResult.Failure(LaunchError.BOTTOM_NOT_FOUND)
        if (!isLaunchable(bottomPkg)) return PlanResult.Failure(LaunchError.BOTTOM_NOT_LAUNCHABLE)
        return PlanResult.Ready(SplitPlan(adjacentPkg = bottomPkg, followPkg = topPkg, delayMs = delayMs))
    }
}
