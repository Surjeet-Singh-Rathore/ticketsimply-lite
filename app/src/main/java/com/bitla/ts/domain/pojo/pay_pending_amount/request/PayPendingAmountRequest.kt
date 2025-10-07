package com.bitla.ts.domain.pojo.pay_pending_amount.request

data class PayPendingAmountRequest(
    val current_user_id: Int,
    val is_partially_paid: Int,
    val payment_type: String,
    val pnr_number: String
){
    var ticket = Ticket()
}