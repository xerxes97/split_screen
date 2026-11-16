package com.example.split_screen

import org.junit.Test
import org.junit.Assert.*
import java.io.File

class SecurityManifestTest {

    private fun readManifest(): String {
        val manifestFile = File("src/main/AndroidManifest.xml")
        return manifestFile.readText()
    }

    @Test
    fun allowBackupIsDisabled() {
        val manifest = readManifest()
        assertTrue("android:allowBackup debe ser 'false'", manifest.contains("android:allowBackup=\"false\""))
    }

    @Test
    fun noInternetPermissionDeclared() {
        val manifest = readManifest()
        assertFalse("No se debe declarar permiso INTERNET", manifest.contains("android.permission.INTERNET"))
    }

    @Test
    fun queriesDeclaredForLaunchableApps() {
        val manifest = readManifest()
        assertTrue("Debe declararse <queries> para consultar apps lanzables", manifest.contains("<queries>"))
        assertTrue("Debe incluir intent con action MAIN y category LAUNCHER", manifest.contains("android.intent.action.MAIN"))
        assertTrue("Debe incluir category android.intent.category.LAUNCHER en queries", manifest.contains("android.intent.category.LAUNCHER"))
    }

    @Test
    fun releaseMinifyAndShrinkEnabled() {
        val buildGradle = File("build.gradle.kts").readText()
        assertTrue("isMinifyEnabled debe ser true en release", buildGradle.contains("isMinifyEnabled = true"))
        assertTrue("isShrinkResources debe ser true en release", buildGradle.contains("isShrinkResources = true"))
    }
}
