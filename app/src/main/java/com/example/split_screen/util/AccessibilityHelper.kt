package com.example.split_screen.util

import android.content.Context
import android.view.accessibility.AccessibilityManager

object AccessibilityHelper {

    /**
     * Valida si el servicio de accesibilidad está activo.
     *
     * Consulta el AccessibilityManager para determinar si hay servicios de
     * accesibilidad habilitados en el sistema. No expone secretos ni depende
     * de componentes de UI.
     *
     * @param context Contexto de la aplicación.
     * @return true si el servicio de accesibilidad está habilitado, false en otro caso.
     */
    fun isServiceActive(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return manager.isEnabled
    }
}
