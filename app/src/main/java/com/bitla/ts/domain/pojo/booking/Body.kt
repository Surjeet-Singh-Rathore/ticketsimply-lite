package com.bitla.ts.domain.pojo.booking

import com.bitla.mba.morningstartravels.mst.pojo.booking.Transaction
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Body {

    @SerializedName("transactions")
    @Expose
    var transactions: List<Transaction>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("cancelled_fare")
    @Expose
    var cancelledFare: String? = null

    @SerializedName("cancel_percent")
    @Expose
    var cancelPercent: String? = null

    @SerializedName("refund_amount")
    @Expose
    var refundAmount: String? = null

    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true
}
