package com.example.split_screen.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.split_screen.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * JVM tests for [ProStatusStorageManager] using an in-memory DataStore.
 * Real file persistence is covered by the instrumented ProStatusStorageManagerInstrumentedTest
 * (file-backed DataStore can't overwrite files on a Windows JVM).
 */
class ProStatusStorageManagerTest {

    private class InMemoryDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())
        private val mutex = Mutex()
        override val data: Flow<Preferences> = state

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences,
        ): Preferences = mutex.withLock {
            transform(state.value).also { state.value = it }
        }
    }

    private class UnreadableDataStore(
        private val error: Exception = IOException("corrupt"),
    ) : DataStore<Preferences> {
        override val data: Flow<Preferences> = flow { throw error }
        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences,
        ): Preferences = throw IOException("corrupt")
    }

    @Test
    fun defaultIsFalseWhenNothingStored() = runBlocking {
        val storage = ProStatusStorageManager(InMemoryDataStore())
        assertEquals(Constants.ProStatus.DEFAULT_IS_PRO_USER, storage.isProUser.first())
        assertFalse(storage.isProUser.first())
    }

    @Test
    fun writeTrueThenReadTrue() = runBlocking {
        val storage = ProStatusStorageManager(InMemoryDataStore())
        storage.setProUser(true)
        assertTrue(storage.isProUser.first())
    }

    @Test
    fun writeTrueThenFalseReadsFalse() = runBlocking {
        val storage = ProStatusStorageManager(InMemoryDataStore())
        storage.setProUser(true)
        storage.setProUser(false)
        assertFalse(storage.isProUser.first())
    }

    @Test
    fun unreadableDataFallsBackToFalse() = runBlocking {
        val storage = ProStatusStorageManager(UnreadableDataStore())
        assertFalse(storage.isProUser.first())
    }

    @Test
    fun unexpectedErrorFallsBackToFalse() = runBlocking {
        val storage = ProStatusStorageManager(UnreadableDataStore(IllegalStateException("boom")))
        assertFalse(storage.isProUser.first())
    }
}
