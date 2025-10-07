package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PreviousPnrHash {
    @SerializedName("previous_pnr")
    @Expose
    var previousPnr: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("route_id")
    @Expose
    var routeId: Int? = null

    @SerializedName("res_id")
    @Expose
    var resId: Long? = null

    @SerializedName("total_fare")
    @Expose
    var totalFare: Double? = null

    @SerializedName("selected_seats")
    @Expose
    var selectedSeats: String? = null

    @SerializedName("booking_type")
    @Expose
    var bookingType: String? = null

    @SerializedName("origin")
    @Expose
    var origin: String? = null

    @SerializedName("destination")
    @Expose
    var destination: String? = null
}