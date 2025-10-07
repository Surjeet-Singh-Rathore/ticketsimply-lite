package com.bitla.ts.domain.pojo.cancellation_policies

import com.bitla.ts.domain.pojo.booking.Body
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CancelDataPojo {
    @SerializedName("body")
    @Expose
    var body: Body? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("success")
    @Expose
    var success: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null


    @SerializedName("refund_amount")
    @Expose
    var refundAmount: String? = null

    @SerializedName("cancellation_charges")
    @Expose
    var cancelCharges: String? = null

    @SerializedName("cancellation_percentage")
    @Expose
    var cancelPercentage: String? = null

    @SerializedName("seat_numbers")
    @Expose
    var seatNumber: String? = null

    @SerializedName("cancellation_refund_percent")
    @Expose
    var cancelRefundPercentage: String? = null


}