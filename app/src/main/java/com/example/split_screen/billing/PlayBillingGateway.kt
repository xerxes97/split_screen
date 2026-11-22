package com.example.split_screen.billing

import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

/** [BillingGateway] backed by Google Play Billing. Call [close] when done. */
class PlayBillingGateway(
    context: Context,
    onPurchasesUpdated: () -> Unit,
) : BillingGateway {

    private val client: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener { _, _ -> onPurchasesUpdated() }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    override suspend fun queryOneTimePurchases(): PurchaseQueryResult {
        if (!connect()) return PurchaseQueryResult.Failure
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val result = client.queryPurchasesAsync(params)
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            return PurchaseQueryResult.Failure
        }
        return PurchaseQueryResult.Success(result.purchasesList.map { it.toSnapshot() })
    }

    override suspend fun acknowledge(token: String): Boolean {
        if (!connect()) return false
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(token).build()
        return client.acknowledgePurchase(params).responseCode == BillingClient.BillingResponseCode.OK
    }

    fun close() = client.endConnection()

    // Only one startConnection in flight; a second one gets DEVELOPER_ERROR.
    private val connectMutex = Mutex()

    private suspend fun connect(): Boolean = connectMutex.withLock {
        if (client.isReady) return@withLock true
        suspendCancellableCoroutine { cont ->
            val resumed = AtomicBoolean(false)
            fun finish(ok: Boolean) {
                if (resumed.compareAndSet(false, true) && cont.isActive) cont.resume(ok)
            }
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) =
                    finish(result.responseCode == BillingClient.BillingResponseCode.OK)

                override fun onBillingServiceDisconnected() = finish(false)
            })
        }
    }

    private fun Purchase.toSnapshot() = PurchaseSnapshot(
        productIds = products,
        state = when (purchaseState) {
            Purchase.PurchaseState.PURCHASED -> PurchaseSnapshot.State.PURCHASED
            Purchase.PurchaseState.PENDING -> PurchaseSnapshot.State.PENDING
            else -> PurchaseSnapshot.State.OTHER
        },
        isAcknowledged = isAcknowledged,
        token = purchaseToken,
    )
}
