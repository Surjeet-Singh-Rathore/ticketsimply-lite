package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SmartMilesHash {
    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("otp_key")
    @Expose
    var otpKey: String? = null

    @SerializedName("otp")
    @Expose
    var otp: String? = null
}