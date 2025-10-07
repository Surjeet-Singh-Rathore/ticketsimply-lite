package com.bitla.ts.domain.pojo.fare_breakup.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SeatWiseFare {
    @SerializedName("seat_no")
    @Expose
    var seatNo: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null
    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null
    @SerializedName("auto_discount_coupon")
    @Expose
    var auto_discount_coupon: String? = null
    @SerializedName("rut_id")
    @Expose
    var rut_id: String? = null
}