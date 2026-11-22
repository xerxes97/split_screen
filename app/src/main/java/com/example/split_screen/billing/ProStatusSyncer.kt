package com.example.split_screen.billing

import com.example.split_screen.Constants
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
    private val productId: String = Constants.Billing.PRODUCT_ID_PRO,
) {

    // Serializes query + write so a stale answer can never overwrite a newer one.
    private val mutex = Mutex()

    /** @return Google's verdict (true = Pro owned), or null when Play gave no reliable answer. */
    suspend fun sync(): Boolean? = mutex.withLock {
        val decision = ProPurchaseEvaluator.evaluate(gateway.queryOneTimePurchases(), productId)
        decision.isPro?.let { storage.setProUser(it) }
        // Failed acknowledgements are retried on the next sync.
        decision.tokensToAcknowledge.forEach { gateway.acknowledge(it) }
        decision.isPro
    }
}
