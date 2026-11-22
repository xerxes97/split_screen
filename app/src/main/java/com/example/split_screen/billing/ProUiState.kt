package com.example.split_screen.billing

/** What the Pro card shows. */
sealed interface ProUiState {
    data object Pro : ProUiState
    data class Free(val formattedPrice: String) : ProUiState

    /** Product not loaded (not created in Play Console yet, or Play unreachable). */
    data object Unavailable : ProUiState

    companion object {
        /** Owning Pro wins over product availability. */
        fun from(isPro: Boolean, product: ProProduct?): ProUiState = when {
            isPro -> Pro
            product != null -> Free(product.formattedPrice)
            else -> Unavailable
        }
    }
}
