package com.example.split_screen.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.split_screen.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/**
 * DataStore-backed [ProStatusStorage]. Missing or unreadable data resolves to
 * [Constants.ProStatus.DEFAULT_IS_PRO_USER] (free tier).
 */
class ProStatusStorageManager(
    private val dataStore: DataStore<Preferences>,
) : ProStatusStorage {

    private val key = booleanPreferencesKey(Constants.ProStatus.KEY_IS_PRO_USER)

    override val isProUser: Flow<Boolean> = dataStore.data
        .catch { error ->
            // Fail closed: any read error means free tier. Never swallow
            // cancellation or fatal VM errors (OutOfMemoryError etc.).
            if (error is CancellationException || error is Error) throw error
            emit(emptyPreferences())
        }
        .map { prefs -> prefs[key] ?: Constants.ProStatus.DEFAULT_IS_PRO_USER }

    override suspend fun setProUser(value: Boolean) {
        dataStore.edit { prefs -> prefs[key] = value }
    }
}
