package com.example.split_screen.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Verifies real file persistence of the Pro status on a device. */
@RunWith(AndroidJUnit4::class)
class ProStatusStorageManagerInstrumentedTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val file = File(context.cacheDir, "pro_status_test.preferences_pb")
    private val scopes = mutableListOf<CoroutineScope>()

    // DataStore allows one active instance per file: cancel the previous one first.
    private fun newDataStore(): DataStore<Preferences> {
        scopes.forEach { it.cancel() }
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scopes += scope
        return PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })
    }

    @After
    fun tearDown() {
        scopes.forEach { it.cancel() }
        file.delete()
    }

    @Test
    fun valuePersistsAcrossInstances() = runBlocking {
        val first = ProStatusStorageManager(newDataStore())
        first.setProUser(true)
        first.setProUser(false)
        first.setProUser(true)

        val reopened = ProStatusStorageManager(newDataStore())
        assertTrue(reopened.isProUser.first())
    }

    @Test
    fun defaultIsFalseOnFreshFile() = runBlocking {
        file.delete()
        assertFalse(ProStatusStorageManager(newDataStore()).isProUser.first())
    }
}
