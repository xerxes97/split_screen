package com.example.split_screen

import org.junit.Test
import org.junit.Assert.*
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File

class AccessibilityConfigTest {

    @Test
    fun accessibilityServiceConfigHasSecurityRestrictions() {
        val configFile = File("src/main/res/xml/accessibility_service_config.xml")
        assertTrue("El archivo accessibility_service_config.xml debe existir", configFile.exists())

        val xml = configFile.readText()

        assertTrue("canRetrieveWindowContent debe ser 'false'", xml.contains("canRetrieveWindowContent=\"false\""))
        assertTrue("canPerformGestures debe ser 'false'", xml.contains("canPerformGestures=\"false\""))
        assertTrue("accessibilityEventTypes debe estar vacío (sin escuchar eventos)", xml.contains("accessibilityEventTypes=\"\""))
    }

    @Test
    fun accessibilityServiceConfigIsWellFormed() {
        val configFile = File("src/main/res/xml/accessibility_service_config.xml")
        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        builder.parse(java.io.ByteArrayInputStream(configFile.readBytes()))
    }
}
