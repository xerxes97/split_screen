package com.example.split_screen.launcher

import org.junit.Assert.assertEquals
import org.junit.Test

class SplitPlannerTest {

    private val top = "com.android.chrome"
    private val bottom = "com.google.android.deskclock"

    private fun plan(
        installed: Set<String> = setOf(top, bottom),
        launchable: Set<String> = setOf(top, bottom)
    ) = SplitPlanner.plan(top, bottom, { it in installed }, { it in launchable }, delayMs = 1000L)

    @Test
    fun bottomOpensAdjacentFirstThenTop() {
        assertEquals(PlanResult.Ready(SplitPlan(adjacentPkg = bottom, followPkg = top, delayMs = 1000L)), plan())
    }

    @Test
    fun missingTopFails() {
        assertEquals(PlanResult.Failure(LaunchError.TOP_NOT_FOUND), plan(installed = setOf(bottom)))
    }

    @Test
    fun topWithoutLauncherFails() {
        assertEquals(PlanResult.Failure(LaunchError.TOP_NOT_LAUNCHABLE), plan(launchable = setOf(bottom)))
    }

    @Test
    fun missingBottomFails() {
        assertEquals(PlanResult.Failure(LaunchError.BOTTOM_NOT_FOUND), plan(installed = setOf(top)))
    }

    @Test
    fun bottomWithoutLauncherFails() {
        assertEquals(PlanResult.Failure(LaunchError.BOTTOM_NOT_LAUNCHABLE), plan(launchable = setOf(top)))
    }
}
