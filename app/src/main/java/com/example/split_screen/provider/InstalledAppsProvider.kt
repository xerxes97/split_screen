package com.example.split_screen.provider

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.split_screen.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Proveedor de lista de aplicaciones instaladas.
 *
 * Lógica separada de UI (MainActivity). Consulta PackageManager en
 * Dispatchers.IO, excluye la propia app y ordena alfabéticamente por nombre.
 */
class InstalledAppsProvider(private val context: Context) {

    /**
     * Obtiene la lista de apps instaladas, excluyendo la propia app,
     * ordenada alfabéticamente por nombre.
     *
     * @return Lista inmutable de [AppInfo] ordenada alfabéticamente por nombre.
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val currentPkg = context.packageName

        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != currentPkg }
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map { appInfo ->
                val name = appInfo.loadLabel(pm).toString()
                AppInfo(name, appInfo.packageName)
            }
            .sortedBy { it.name.lowercase() }

        apps
    }
}