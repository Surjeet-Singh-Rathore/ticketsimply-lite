package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CouponCodeHash {
    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null


}