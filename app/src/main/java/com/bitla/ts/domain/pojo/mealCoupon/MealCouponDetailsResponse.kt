package com.bitla.ts.domain.pojo.mealCoupon

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class MealCouponDetailsResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("seat_details")
    @Expose
    var seatDetails: ArrayList<SeatDetails>? = null
}