package com.example.split_screen.billing

/**
 * Result of evaluating a purchase query.
 *
 * @property isPro value to store, or null to keep the cached value unchanged.
 * @property tokensToAcknowledge purchases that still need acknowledgement
 *   (Google refunds unacknowledged purchases after 3 days).
 */
data class ProDecision(
    val isPro: Boolean?,
    val tokensToAcknowledge: List<String>,
)

/** Pure rules that turn Google's answer into the Pro status. */
object ProPurchaseEvaluator {

    fun evaluate(result: PurchaseQueryResult, productId: String): ProDecision {
        if (result !is PurchaseQueryResult.Success) {
            return ProDecision(isPro = null, tokensToAcknowledge = emptyList())
        }
        val owned = result.purchases.filter {
            productId in it.productIds && it.state == PurchaseSnapshot.State.PURCHASED
        }
        return ProDecision(
            isPro = owned.isNotEmpty(),
            tokensToAcknowledge = owned.filterNot { it.isAcknowledged }.map { it.token },
        )
    }
}
