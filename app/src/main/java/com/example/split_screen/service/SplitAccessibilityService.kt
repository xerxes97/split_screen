package com.example.split_screen.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

/**
 * Servicio de accesibilidad neutralizado por diseño.
 *
 * Este servicio existe en el manifiesto pero no puede:
 * - Leer contenido de ventanas de otras apps (canRetrieveWindowContent="false")
 * - Realizar gestos automáticos (canPerformGestures="false")
 * - Escuchar eventos de accesibilidad (accessibilityEventTypes="")
 *
 * Defensa en profundidad: aunque esté declarado, es inofensivo para las apps
 * que se ejecutan en pantalla dividida.
 */
class SplitAccessibilityService : AccessibilityService() {

    /** Instancia aislada, solo accesible dentro de esta clase. */
    private companion object {
        @Volatile private var instance: SplitAccessibilityService? = null
    }

    /**
     * Obtiene la instancia única del servicio de forma controlada.
     *
     * @return La instancia actual de [SplitAccessibilityService], o null si no está conectado.
     */
    fun getInstance(): SplitAccessibilityService? = instance

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onUnbind(intent: android.content.Intent): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    /**
     * Callback de eventos de accesibilidad.
     *
     * Queda vacío intencionalmente porque la configuración tiene
     * [android.accessibilityEventTypes] vacío, por lo que nunca se invoca.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // No-op: servicio no escucha eventos de accesibilidad.
    }

    /**
     * Callback de interrupción de accesibilidad.
     *
     * Queda vacío intencionalmente porque el servicio no realiza gestos ni
     * interactúa con otras apps.
     */
    override fun onInterrupt() {
        // No-op: servicio no realiza gestos automáticos.
    }
}
