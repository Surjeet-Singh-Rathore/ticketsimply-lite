package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PromotionCouponHash {
    @SerializedName("pnr_number")
    @Expose
    var pnrNumber: String? = null


    @SerializedName("promotion_coupon_code")
    @Expose
    var promotionCouponCode: String? = null

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String = ""

    @SerializedName("is_round_trip")
    @Expose
    var isRoundTrip: Boolean = false

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null



    @SerializedName("journey_date")
    @Expose
    var journeyDate: String = ""

    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: String? = null

    @SerializedName("total_fare")
    @Expose
    var totalFare: String = ""

}