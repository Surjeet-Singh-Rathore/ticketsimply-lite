package com.bitla.ts.domain.pojo.book_ticket_full.request

data class WalletPaymentHash(
    val allow_wallet_booking: Boolean,
    val selected_wallet: String,
    val wallet_mobile_number: String
)