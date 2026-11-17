package com.example.split_screen.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
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

    /**
     * Lanza dos aplicaciones en pantalla dividida (split-screen).
     *
     * Secuencia:
     * 1. Lanza la app superior ([topPkg]) con un [Intent] estándar.
     * 2. Espera [delayMs] milisegundos para que la app se cargue.
     * 3. Invoca [GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN] vía [performGlobalAction]
     *    para activar el modo pantalla dividida del sistema.
     * 4. Lanza la app inferior ([bottomPkg]) con
     *    [Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT] para que el sistema la
     *    coloque en la mitad inferior de la pantalla.
     *
     * @param topPkg    Package name de la app superior (ej. "com.android.chrome").
     * @param bottomPkg Package name de la app inferior (ej. "com.whatsapp").
     * @param delayMs   Milisegundos de espera entre lanzar la app superior y
     *                  hacer el toggle split-screen (para que la app cargue).
     *
     * @throws IllegalStateException si el servicio no está conectado.
     */
    fun launchSplitScreen(topPkg: String, bottomPkg: String, delayMs: Long = 1500) {
        if (instance == null) {
            throw IllegalStateException("AccessibilityService no está conectado")
        }

        // 1. Lanza la app superior
        val topIntent = packageManager.getLaunchIntentForPackage(topPkg)
            ?: throw IllegalArgumentException("No se pudo crear intent para $topPkg")
        topIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(topIntent)

        // 2. Espera delayMs y luego hace el toggle + lanza app inferior
        Handler(Looper.getMainLooper()).postDelayed({
            // 3. Activa split-screen via acción global de accesibilidad
            performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

            // 4. Lanza app inferior con FLAG_ACTIVITY_LAUNCH_ADJACENT
            val bottomIntent = packageManager.getLaunchIntentForPackage(bottomPkg)
                ?: throw IllegalArgumentException("No se pudo crear intent para $bottomPkg")
            bottomIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
            startActivity(bottomIntent)
        }, delayMs)
    }
}
