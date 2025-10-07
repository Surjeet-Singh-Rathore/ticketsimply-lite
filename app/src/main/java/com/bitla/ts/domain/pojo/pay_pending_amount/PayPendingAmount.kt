package com.bitla.ts.domain.pojo.pay_pending_amount

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PayPendingAmount {
    @SerializedName(":message")
    @Expose
    var message: String? = null

    @SerializedName(":is_partial_payment")
    @Expose
    var isPartialPayment: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("result")
    @Expose
    var result: Result? = null
}