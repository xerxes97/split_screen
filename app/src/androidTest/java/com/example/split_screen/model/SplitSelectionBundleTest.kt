package com.example.split_screen.model

import android.os.Bundle
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplitSelectionBundleTest {

    private val chrome = AppInfo("Chrome", "com.android.chrome")
    private val whatsapp = AppInfo("WhatsApp", "com.whatsapp")

    @Test
    fun readFromNullBundleReturnsEmptySelection() {
        assertEquals(SplitSelection(), SplitSelection.readFrom(null))
    }

    @Test
    fun selectionSurvivesParcelRoundTrip() {
        val original = SplitSelection(top = chrome, bottom = whatsapp)
        val bundle = Bundle().also { original.writeTo(it) }

        // Simula guardado/restauración real de instance state.
        val parcel = Parcel.obtain()
        bundle.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val restoredBundle = Bundle.CREATOR.createFromParcel(parcel)
        parcel.recycle()
        restoredBundle.classLoader = AppInfo::class.java.classLoader

        assertEquals(original, SplitSelection.readFrom(restoredBundle))
    }

    @Test
    fun partialSelectionSurvivesRoundTrip() {
        val original = SplitSelection(top = chrome)
        val bundle = Bundle().also { original.writeTo(it) }
        assertEquals(original, SplitSelection.readFrom(bundle))
    }
}
