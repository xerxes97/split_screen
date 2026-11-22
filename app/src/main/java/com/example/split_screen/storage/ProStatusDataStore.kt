package com.example.split_screen.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.split_screen.Constants

/**
 * Single app-wide DataStore for the Pro status. Default location
 * files/datastore/pro_status.preferences_pb matches data_extraction_rules.xml.
 */
val Context.proStatusDataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.ProStatus.DATASTORE_NAME,
)
