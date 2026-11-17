package com.example.split_screen.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.example.split_screen.service.SplitAccessibilityService

object AccessibilityHelper {

    private const val SERVICES_SEPARATOR = ':'
    private const val COMPONENT_SEPARATOR = '/'

    /**
     * Indica si [SplitAccessibilityService] de esta app está habilitado y conectado.
     *
     * No usa AccessibilityManager.isEnabled: ese valor es true si
     * cualquier servicio de accesibilidad del sistema está activo (p. ej. TalkBack).
     *
     * @param context Contexto de la aplicación.
     * @return true solo si nuestro servicio está habilitado en Ajustes y el
     *         sistema lo tiene conectado (no caído ni sin enlazar).
     */
    fun isServiceActive(context: Context): Boolean {
        val packageName = context.packageName
        val className = SplitAccessibilityService::class.java.name
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (!isServiceInList(enabled, packageName, className)) return false

        val manager = context.getSystemService(AccessibilityManager::class.java) ?: return false
        val connected = manager
            .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .mapNotNull { info ->
                info.resolveInfo?.serviceInfo?.let { it.packageName to it.name }
            }
        return isServiceConnected(connected, packageName, className)
    }

    /**
     * Busca el servicio entre los conectados por el sistema.
     *
     * @param connected Pares (paquete, clase completa) de los servicios conectados.
     */
    internal fun isServiceConnected(
        connected: List<Pair<String, String>>,
        packageName: String,
        className: String
    ): Boolean = connected.any { (pkg, cls) -> pkg == packageName && cls == className }

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
