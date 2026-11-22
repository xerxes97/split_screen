package com.example.split_screen.billing

/** Play-independent view of a purchase, so the sync logic is testable on the JVM. */
data class PurchaseSnapshot(
    val productIds: List<String>,
    val state: State,
    val isAcknowledged: Boolean,
    val token: String,
) {
    enum class State { PURCHASED, PENDING, OTHER }
}

sealed interface PurchaseQueryResult {
    /** Google answered OK. An empty list means the user owns nothing. */
    data class Success(val purchases: List<PurchaseSnapshot>) : PurchaseQueryResult

    /** No reliable answer (offline, Play unavailable, error). */
    data object Failure : PurchaseQueryResult
}

/** Purchasable Pro product as shown to the user. */
data class ProProduct(val formattedPrice: String)

/** Abstraction over Google Play Billing. */
interface BillingGateway {
    suspend fun queryOneTimePurchases(): PurchaseQueryResult

    /** @return the product, or null if it does not exist in Play Console or Play is unreachable. */
    suspend fun queryProProduct(productId: String): ProProduct?

    /** @return true when Google confirmed the acknowledgement. */
    suspend fun acknowledge(token: String): Boolean
}
