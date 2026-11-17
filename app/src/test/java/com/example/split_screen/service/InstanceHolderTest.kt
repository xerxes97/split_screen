package com.example.split_screen.service

import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class InstanceHolderTest {

    private class Fake

    @Test
    fun startsEmpty() {
        assertNull(InstanceHolder<Fake>().current())
    }

    @Test
    fun attachExposesInstance() {
        val holder = InstanceHolder<Fake>()
        val a = Fake()
        holder.attach(a)
        assertSame(a, holder.current())
    }

    @Test
    fun detachClearsInstance() {
        val holder = InstanceHolder<Fake>()
        val a = Fake()
        holder.attach(a)
        holder.detach(a)
        assertNull(holder.current())
    }

    @Test
    fun staleDetachKeepsNewInstance() {
        val holder = InstanceHolder<Fake>()
        val old = Fake()
        val new = Fake()
        holder.attach(old)
        holder.attach(new)
        holder.detach(old)
        assertSame(new, holder.current())
    }
}
