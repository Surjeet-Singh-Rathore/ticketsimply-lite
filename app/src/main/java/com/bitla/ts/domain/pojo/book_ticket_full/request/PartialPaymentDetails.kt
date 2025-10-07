package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PartialPaymentDetails {
    @SerializedName("partial_amount")
    @Expose
    var partialAmount: Any? = null

    @SerializedName("pending_amount")
    @Expose
    var pendingAmount: Any? = null

    @SerializedName("option")
    @Expose
    var option: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("blocking_date")
    @Expose
    var blockingDate: Any? = null

    @SerializedName("time_hours")
    @Expose
    var timeHours: Any? = null

    @SerializedName("time_mins")
    @Expose
    var timeMins: Any? = null
}