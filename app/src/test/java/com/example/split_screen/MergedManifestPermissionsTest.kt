package com.example.split_screen

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The app itself must not request network permissions. Google Play Billing
 * (and its transitive datatransport telemetry) adds INTERNET and
 * ACCESS_NETWORK_STATE; those are the only accepted sources.
 * Reads the manifest merger blame report produced by processDebugMainManifest.
 */
class MergedManifestPermissionsTest {

    private val report = File("build/outputs/logs/manifest-merger-debug-report.txt")
    private val allowedGroups = listOf("com.android.billingclient", "com.google.android.datatransport")
    private val networkPermissions = listOf(
        "android.permission.INTERNET",
        "android.permission.ACCESS_NETWORK_STATE",
    )

    /** Sources ("ADDED from"/"MERGED from" lines) of [permission] in the blame report. */
    private fun sourcesOf(permission: String): List<String> {
        val lines = report.readLines()
        val start = lines.indexOfFirst { it == "uses-permission#$permission" }
        if (start < 0) return emptyList()
        return lines.drop(start + 1)
            .takeWhile { it.startsWith("ADDED from") || it.startsWith("MERGED from") || it.startsWith("\t") }
            .filter { it.startsWith("ADDED from") || it.startsWith("MERGED from") }
    }

    @Test
    fun networkPermissionsComeOnlyFromBilling() {
        // Unit test tasks depend on processDebugMainManifest (app/build.gradle.kts).
        assertTrue("Missing manifest merger report: ${report.path}", report.exists())
        // Billing always adds INTERNET: proves the report parser still matches the format.
        assertTrue("Report format changed", sourcesOf("android.permission.INTERNET").isNotEmpty())
        networkPermissions.forEach { permission ->
            sourcesOf(permission).forEach { source ->
                assertTrue(
                    "$permission added by unexpected source: $source",
                    allowedGroups.any { group -> source.contains("[$group:") },
                )
            }
        }
    }
}
