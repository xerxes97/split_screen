package com.example.split_screen.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class SplitSelectionTest {

    private val chrome = AppInfo("Chrome", "com.android.chrome")
    private val whatsapp = AppInfo("WhatsApp", "com.whatsapp")
    private val maps = AppInfo("Maps", "com.google.android.apps.maps")

    @Test
    fun initialSelectionIsEmpty() {
        val selection = SplitSelection()
        assertNull(selection.top)
        assertNull(selection.bottom)
    }

    @Test
    fun selectTopAndBottomStoresBothApps() {
        val selection = SplitSelection().selectTop(chrome).selectBottom(whatsapp)
        assertEquals(chrome, selection.top)
        assertEquals(whatsapp, selection.bottom)
    }

    @Test
    fun selectBottomWithSameAppAsTopIsIgnored() {
        val withTop = SplitSelection().selectTop(chrome)
        assertSame(withTop, withTop.selectBottom(chrome))
    }

    @Test
    fun selectTopWithSameAppAsBottomIsIgnored() {
        val withBottom = SplitSelection().selectBottom(whatsapp)
        assertSame(withBottom, withBottom.selectTop(whatsapp))
    }

    @Test
    fun selectTopReplacesPreviousTopApp() {
        val selection = SplitSelection().selectTop(chrome).selectTop(maps)
        assertEquals(maps, selection.top)
    }

    @Test
    fun availableListsExcludeAppInOtherPosition() {
        val apps = listOf(chrome, whatsapp, maps)
        val selection = SplitSelection().selectTop(chrome).selectBottom(whatsapp)
        assertEquals(listOf(chrome, maps), selection.availableForTop(apps))
        assertEquals(listOf(whatsapp, maps), selection.availableForBottom(apps))
    }

    @Test
    fun availableListsReturnAllAppsWhenNothingSelected() {
        val apps = listOf(chrome, whatsapp)
        assertEquals(apps, SplitSelection().availableForTop(apps))
        assertEquals(apps, SplitSelection().availableForBottom(apps))
    }
}
