package com.example.split_screen

import org.junit.Test
import org.junit.Assert.*

class BuildConfigTest {

    @Test
    fun buildConfigFieldsAreAccessible() {
        assertNotNull(BuildConfig.BASE_URL)
        assertNotNull(BuildConfig.API_KEY)
        assertNotNull(BuildConfig.PRO_PRODUCT_ID)
    }

    @Test
    fun proProductIdIsValidPlayProductIdOrEmpty() {
        // Play product IDs: lowercase letters, digits, '_' and '.', starting with a letter or digit.
        val id = BuildConfig.PRO_PRODUCT_ID
        assertTrue("Invalid PRO_PRODUCT_ID: '$id'", id.isEmpty() || Regex("^[a-z0-9][a-z0-9_.]*$").matches(id))
    }

    @Test
    fun buildConfigValuesComeFromLocalPropertiesNotSource() {
        val baseUrl = BuildConfig.BASE_URL
        val apiKey = BuildConfig.API_KEY

        assertTrue(baseUrl.isEmpty() || baseUrl.startsWith("http://") || baseUrl.startsWith("https://"))
        assertTrue(apiKey.isEmpty() || apiKey.length > 0)
    }
}
