package com.example.split_screen.util

import android.content.Context
import android.provider.Settings
import com.example.split_screen.service.SplitAccessibilityService

object AccessibilityHelper {

    private const val SERVICES_SEPARATOR = ':'
    private const val COMPONENT_SEPARATOR = '/'

    /**
     * Indica si [SplitAccessibilityService] de esta app está habilitado.
     *
     * No usa AccessibilityManager.isEnabled: ese valor es true si
     * cualquier servicio de accesibilidad del sistema está activo (p. ej. TalkBack).
     *
     * @param context Contexto de la aplicación.
     * @return true solo si nuestro servicio está habilitado en Ajustes.
     */
    fun isServiceActive(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return isServiceInList(
            enabledServices = enabled,
            packageName = context.packageName,
            className = SplitAccessibilityService::class.java.name
        )
    }

    /**
     * Busca un servicio en la lista de Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES.
     *
     * Formato: componentes separados por ':', cada uno "paquete/clase"; la clase
     * puede venir completa o abreviada con '.' inicial (relativa al paquete).
     */
    internal fun isServiceInList(enabledServices: String?, packageName: String, className: String): Boolean {
        if (enabledServices.isNullOrBlank()) return false
        return enabledServices.split(SERVICES_SEPARATOR).any { entry ->
            val parts = entry.trim().split(COMPONENT_SEPARATOR)
            if (parts.size != 2 || parts[0] != packageName) return@any false
            val cls = parts[1]
            val fullClass = if (cls.startsWith(".")) packageName + cls else cls
            fullClass == className
        }
    }
}
