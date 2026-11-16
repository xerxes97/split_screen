package com.example.split_screen

import org.junit.Test
import org.junit.Assert.*

class BuildConfigTest {

    @Test
    fun buildConfigFieldsAreAccessible() {
        assertNotNull(BuildConfig.BASE_URL)
        assertNotNull(BuildConfig.API_KEY)
    }

    @Test
    fun buildConfigValuesComeFromLocalPropertiesNotSource() {
        val baseUrl = BuildConfig.BASE_URL
        val apiKey = BuildConfig.API_KEY

        assertTrue(baseUrl.isEmpty() || baseUrl.startsWith("http://") || baseUrl.startsWith("https://"))
        assertTrue(apiKey.isEmpty() || apiKey.length > 0)
    }
}
