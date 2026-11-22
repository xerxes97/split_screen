package com.example.split_screen.billing

import com.example.split_screen.billing.PurchaseSnapshot.State
import com.example.split_screen.storage.ProStatusStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProStatusSyncerTest {

    private val productId = "test_pro"

    private class FakeStorage(initial: Boolean) : ProStatusStorage {
        val state = MutableStateFlow(initial)
        override val isProUser: Flow<Boolean> = state
        override suspend fun setProUser(value: Boolean) { state.value = value }
    }

    private class FakeGateway(
        private val result: PurchaseQueryResult,
        private val ackSucceeds: Boolean = true,
    ) : BillingGateway {
        val acknowledged = mutableListOf<String>()
        override suspend fun queryOneTimePurchases() = result
        override suspend fun queryProProduct(productId: String): ProProduct? = null
        override suspend fun acknowledge(token: String): Boolean {
            acknowledged += token
            return ackSucceeds
        }
    }

    private fun purchase(
        state: State = State.PURCHASED,
        acknowledged: Boolean = true,
        product: String = productId,
        token: String = "token",
    ) = PurchaseSnapshot(listOf(product), state, acknowledged, token)

    private fun sync(gateway: FakeGateway, storage: FakeStorage) = runBlocking {
        ProStatusSyncer(gateway, storage, productId).sync()
    }

    @Test
    fun purchasedGrantsPro() {
        val storage = FakeStorage(initial = false)
        sync(FakeGateway(PurchaseQueryResult.Success(listOf(purchase()))), storage)
        assertTrue(storage.state.value)
    }

    @Test
    fun noPurchaseRevokesTamperedLocalTrue() {
        val storage = FakeStorage(initial = true)
        sync(FakeGateway(PurchaseQueryResult.Success(emptyList())), storage)
        assertFalse(storage.state.value)
    }

    @Test
    fun failureKeepsCachedValue() {
        val storage = FakeStorage(initial = true)
        sync(FakeGateway(PurchaseQueryResult.Failure), storage)
        assertTrue(storage.state.value)
    }

    @Test
    fun pendingDoesNotGrantPro() {
        val storage = FakeStorage(initial = false)
        val gateway = FakeGateway(PurchaseQueryResult.Success(listOf(purchase(State.PENDING, acknowledged = false))))
        sync(gateway, storage)
        assertFalse(storage.state.value)
        assertTrue(gateway.acknowledged.isEmpty())
    }

    @Test
    fun otherProductDoesNotGrantPro() {
        val storage = FakeStorage(initial = false)
        sync(FakeGateway(PurchaseQueryResult.Success(listOf(purchase(product = "other")))), storage)
        assertFalse(storage.state.value)
    }

    @Test
    fun unacknowledgedPurchaseIsAcknowledged() {
        val gateway = FakeGateway(PurchaseQueryResult.Success(listOf(purchase(acknowledged = false, token = "t1"))))
        sync(gateway, FakeStorage(initial = false))
        assertEquals(listOf("t1"), gateway.acknowledged)
    }

    @Test
    fun acknowledgedPurchaseIsNotAcknowledgedAgain() {
        val gateway = FakeGateway(PurchaseQueryResult.Success(listOf(purchase(acknowledged = true))))
        sync(gateway, FakeStorage(initial = false))
        assertTrue(gateway.acknowledged.isEmpty())
    }

    @Test
    fun concurrentSyncsCannotApplyStaleAnswerLast() = runBlocking {
        val storage = FakeStorage(initial = false)
        val firstAnswerGate = CompletableDeferred<Unit>()
        var calls = 0
        val gateway = object : BillingGateway {
            override suspend fun queryOneTimePurchases(): PurchaseQueryResult {
                calls++
                if (calls == 1) {
                    // Stale answer: Pro, delayed until the second sync is waiting.
                    firstAnswerGate.await()
                    return PurchaseQueryResult.Success(listOf(purchase()))
                }
                // Newer answer: refunded.
                return PurchaseQueryResult.Success(emptyList())
            }
            override suspend fun acknowledge(token: String) = true
            override suspend fun queryProProduct(productId: String): ProProduct? = null
        }
        val syncer = ProStatusSyncer(gateway, storage, productId)
        val first = launch { syncer.sync() }
        yield()
        val second = launch { syncer.sync() }
        yield()
        firstAnswerGate.complete(Unit)
        first.join()
        second.join()
        assertFalse(storage.state.value)
    }

    @Test
    fun failedAcknowledgementStillGrantsPro() {
        val storage = FakeStorage(initial = false)
        val gateway = FakeGateway(
            PurchaseQueryResult.Success(listOf(purchase(acknowledged = false))),
            ackSucceeds = false,
        )
        sync(gateway, storage)
        assertTrue(storage.state.value)
    }
}
