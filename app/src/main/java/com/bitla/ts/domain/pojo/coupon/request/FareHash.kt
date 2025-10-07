package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class FareHash {
    @SerializedName("seat_no")
    @Expose
    var seatNo: String = ""

    @SerializedName("fare")
    @Expose
    var fare: String = ""


}