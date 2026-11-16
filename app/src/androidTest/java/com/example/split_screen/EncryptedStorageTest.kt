package com.example.split_screen

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedStorageTest {

    @Test
    fun encryptedSharedPreferencesEncryptsAndDecrypts() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "test_master_key",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .build()

        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val prefs = EncryptedSharedPreferences.create(
            "test_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val testKey = "test_key"
        val testValue = "test_value_not_a_secret"

        prefs.edit().putString(testKey, testValue).apply()

        val retrieved = prefs.getString(testKey, null)

        assertEquals(testValue, retrieved)
    }
}
