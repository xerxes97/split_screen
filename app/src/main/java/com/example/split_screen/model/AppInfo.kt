package com.example.split_screen.model

/**
 * Modelo inmutable de información de una aplicación instalada.
 *
 * No guarda íconos (Drawable/Bitmap) para evitar consumo de memoria.
 * El ícono se carga bajo demanda vía [PackageManager.getApplicationIcon].
 */
data class AppInfo(
    val name: String,
    val packageName: String
) {
    override fun toString(): String = "AppInfo(name='$name', packageName='$packageName')"
}