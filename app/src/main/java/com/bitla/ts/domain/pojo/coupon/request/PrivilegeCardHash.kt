package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PrivilegeCardHash {
    @SerializedName("card_number")
    @Expose
    var cardNumber: String? = null

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("reservation_id")
    @Expose
    var resId: String? = null

    @SerializedName("selected_seats")
    @Expose
    var selectedSeats: String? = null

    @SerializedName("return_res_id")
    @Expose
    var returnResId: String? = null

    @SerializedName("is_roundtrip")
    @Expose
    var isRoundtrip: String? = null

    @SerializedName("return_res_seats_count")
    @Expose
    var returnResSeatsCount: String? = null

    @SerializedName("connecting_res_seats_count")
    @Expose
    var connectingResSeatsCount: String? = null
}