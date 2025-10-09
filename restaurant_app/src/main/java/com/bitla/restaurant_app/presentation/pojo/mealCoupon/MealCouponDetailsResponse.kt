package com.bitla.restaurant_app.presentation.pojo.mealCoupon

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class MealCouponDetailsResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("result")
    @Expose
    var result: Result? = null

    @SerializedName("seat_details")
    @Expose
    var seatDetails: ArrayList<SeatDetails>? = null
}

data class Result(val message:String?=null)