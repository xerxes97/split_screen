package com.example.split_screen.launcher

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper

/**
 * Abre dos apps en pantalla dividida desde una Activity visible.
 *
 * 1. Abre la app inferior con LAUNCH_ADJACENT: el sistema divide la pantalla
 *    entre nuestra Activity (arriba) y esa app (abajo).
 * 2. Tras una espera, abre la app superior; reemplaza nuestra mitad.
 */
class SplitLauncher(private val activity: Activity) {

    private val handler = Handler(Looper.getMainLooper())

    fun launch(topPkg: String, bottomPkg: String): PlanResult {
        val pm = activity.packageManager
        val result = SplitPlanner.plan(
            topPkg = topPkg,
            bottomPkg = bottomPkg,
            isInstalled = { isInstalled(pm, it) },
            isLaunchable = { pm.getLaunchIntentForPackage(it) != null }
        )
        if (result !is PlanResult.Ready) return result
        val plan = result.plan
        val adjacent = launchIntent(pm, plan.adjacentPkg, Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
            ?: return PlanResult.Failure(LaunchError.BOTTOM_NOT_LAUNCHABLE)
        if (!start(adjacent)) return PlanResult.Failure(LaunchError.BOTTOM_NOT_LAUNCHABLE)
        handler.postDelayed({
            launchIntent(pm, plan.followPkg, 0)?.let(::start)
        }, plan.delayMs)
        return result
    }

    /** Cancela la segunda apertura pendiente (p. ej. al destruir la Activity). */
    fun cancel() = handler.removeCallbacksAndMessages(null)

    private fun launchIntent(pm: PackageManager, pkg: String, extraFlags: Int): Intent? =
        pm.getLaunchIntentForPackage(pkg)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or extraFlags)
        }

    private fun start(intent: Intent): Boolean = try {
        activity.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    } catch (e: SecurityException) {
        false
    }

    private fun isInstalled(pm: PackageManager, pkg: String): Boolean = try {
        pm.getPackageInfo(pkg, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
