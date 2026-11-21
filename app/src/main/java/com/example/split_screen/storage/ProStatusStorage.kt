package com.example.split_screen.storage

import kotlinx.coroutines.flow.Flow

/**
 * Local cache of the user's Pro status.
 *
 * This is NOT the source of truth: Google Play Billing is. Callers must
 * overwrite this value with the result of each purchase check.
 */
interface ProStatusStorage {
    val isProUser: Flow<Boolean>
    suspend fun setProUser(value: Boolean)
}
