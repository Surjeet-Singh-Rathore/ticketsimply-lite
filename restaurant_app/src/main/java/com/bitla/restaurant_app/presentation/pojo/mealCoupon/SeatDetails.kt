package com.bitla.restaurant_app.presentation.pojo.mealCoupon

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SeatDetails {
    @SerializedName("passenger_name")
    @Expose
    var passengerName: String? = null

    @SerializedName("age")
    @Expose
    var age: Int? = null

    @SerializedName("seat_number")
    @Expose
    var seatNumber: String? = null

    @SerializedName("pnr_number")
    @Expose
    var pnrNumber: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("service_name")
    @Expose
    var serviceName: String? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null
}