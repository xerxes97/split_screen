package com.example.split_screen.billing

import org.junit.Assert.assertEquals
import org.junit.Test

class ProUiStateTest {

    private val product = ProProduct(formattedPrice = "US$ 2.99")

    @Test
    fun freeUserWithProductSeesPrice() {
        assertEquals(ProUiState.Free("US$ 2.99"), ProUiState.from(isPro = false, product = product))
    }

    @Test
    fun freeUserWithoutProductSeesUnavailable() {
        assertEquals(ProUiState.Unavailable, ProUiState.from(isPro = false, product = null))
    }

    @Test
    fun proUserSeesPro() {
        assertEquals(ProUiState.Pro, ProUiState.from(isPro = true, product = product))
    }

    @Test
    fun proWinsWhenProductUnavailable() {
        assertEquals(ProUiState.Pro, ProUiState.from(isPro = true, product = null))
    }
}
