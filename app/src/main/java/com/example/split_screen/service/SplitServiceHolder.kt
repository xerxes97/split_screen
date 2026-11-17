package com.example.split_screen.service

/**
 * Referencia controlada a una instancia viva.
 *
 * Solo la instancia registrada puede borrarse a sí misma: un [detach] tardío
 * de una instancia vieja no borra una instancia nueva ya registrada.
 */
internal class InstanceHolder<T : Any> {

    @Volatile private var instance: T? = null

    fun attach(value: T) {
        instance = value
    }

    @Synchronized
    fun detach(value: T) {
        if (instance === value) instance = null
    }

    fun current(): T? = instance
}

/** Servicio de accesibilidad conectado, accesible solo dentro del módulo. */
internal object SplitServiceHolder {
    private val holder = InstanceHolder<SplitAccessibilityService>()

    internal fun attach(service: SplitAccessibilityService) = holder.attach(service)

    internal fun detach(service: SplitAccessibilityService) = holder.detach(service)

    /** Servicio conectado, o null si el usuario no lo activó o el sistema lo desconectó. */
    fun current(): SplitAccessibilityService? = holder.current()
}
