package com.example.split_screen.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
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

    // Last product fetched; required by launchBillingFlow.
    @Volatile
    private var proDetails: ProductDetails? = null

    override suspend fun queryProProduct(productId: String): ProProduct? {
        if (!connect()) return null
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val params = QueryProductDetailsParams.newBuilder().setProductList(listOf(product)).build()
        val result = client.queryProductDetails(params)
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) return null
        val details = result.productDetailsList?.firstOrNull { it.productId == productId } ?: return null
        val price = details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.formattedPrice ?: return null
        proDetails = details
        return ProProduct(price)
    }

    /**
     * Opens Google's purchase screen. The outcome arrives through the
     * purchases listener, which triggers a sync.
     * @return false if the product was not loaded or Play refused to open.
     */
    fun launchPurchase(activity: Activity): Boolean {
        val details = proDetails ?: return false
        val offerToken = details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .apply { offerToken?.let { setOfferToken(it) } }
            .build()
        val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productParams)).build()
        return client.launchBillingFlow(activity, params).responseCode == BillingClient.BillingResponseCode.OK
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
