package com.example.split_screen.billing

import com.example.split_screen.BuildConfig
import com.example.split_screen.storage.ProStatusStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Makes Google Play Billing the source of truth: overwrites the local cache
 * with Google's answer. On failure the cache is left untouched so a paying
 * user offline keeps Pro.
 */
class ProStatusSyncer(
    private val gateway: BillingGateway,
    private val storage: ProStatusStorage,
    private val productId: String = BuildConfig.PRO_PRODUCT_ID,
) {

    // Serializes query + write so a stale answer can never overwrite a newer one.
    private val mutex = Mutex()

    suspend fun sync(): Unit = mutex.withLock {
        // Missing PRO_PRODUCT_ID in local.properties: never revoke a paid Pro by misconfiguration.
        if (productId.isBlank()) return@withLock
        val decision = ProPurchaseEvaluator.evaluate(gateway.queryOneTimePurchases(), productId)
        decision.isPro?.let { storage.setProUser(it) }
        // Failed acknowledgements are retried on the next sync.
        decision.tokensToAcknowledge.forEach { gateway.acknowledge(it) }
    }
}
