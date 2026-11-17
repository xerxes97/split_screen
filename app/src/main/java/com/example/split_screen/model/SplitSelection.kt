package com.example.split_screen.model

import android.os.Bundle
import androidx.core.os.BundleCompat
import com.example.split_screen.Constants

/**
 * Selección inmutable de las dos apps para pantalla dividida.
 *
 * Regla: la misma app (mismo packageName) no puede ocupar ambas posiciones.
 * Una selección que viole la regla se ignora y devuelve la instancia actual.
 */
data class SplitSelection(
    val top: AppInfo? = null,
    val bottom: AppInfo? = null
) {

    /** Devuelve una nueva selección con [app] arriba, o la actual si ya está abajo. */
    fun selectTop(app: AppInfo): SplitSelection =
        if (app.packageName == bottom?.packageName) this else copy(top = app)

    /** Devuelve una nueva selección con [app] abajo, o la actual si ya está arriba. */
    fun selectBottom(app: AppInfo): SplitSelection =
        if (app.packageName == top?.packageName) this else copy(bottom = app)

    /** Apps elegibles para la posición superior (excluye la app inferior). */
    fun availableForTop(apps: List<AppInfo>): List<AppInfo> =
        apps.filter { it.packageName != bottom?.packageName }

    /** Apps elegibles para la posición inferior (excluye la app superior). */
    fun availableForBottom(apps: List<AppInfo>): List<AppInfo> =
        apps.filter { it.packageName != top?.packageName }

    /** Guarda la selección en [bundle] para sobrevivir a rotación/muerte de proceso. */
    fun writeTo(bundle: Bundle) {
        bundle.putParcelable(Constants.Picker.STATE_TOP, top)
        bundle.putParcelable(Constants.Picker.STATE_BOTTOM, bottom)
    }

    companion object {
        /** Restaura una selección guardada con [writeTo]; vacía si [bundle] es null. */
        fun readFrom(bundle: Bundle?): SplitSelection {
            if (bundle == null) return SplitSelection()
            return SplitSelection(
                top = BundleCompat.getParcelable(bundle, Constants.Picker.STATE_TOP, AppInfo::class.java),
                bottom = BundleCompat.getParcelable(bundle, Constants.Picker.STATE_BOTTOM, AppInfo::class.java)
            )
        }
    }
}
