package com.bitla.ts.domain.pojo.passenger_details_result


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("boarding_at")
    val boardingAt: String,
    @SerializedName("booking_details")
    val bookingDetails: BookingDetails,
    @SerializedName("contact_detail")
    val contactDetail: ContactDetail,
    @SerializedName("coupon_details")
    val couponDetails: List<Any>,
    @SerializedName("destination_id")
    val destinationId: String,
    @SerializedName("drop_off")
    val dropOff: String,
    @SerializedName("is_from_bus_opt_app")
    val isFromBusOptApp: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: String,
    @SerializedName("is_rapid_booking")
    val isRapidBooking: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("no_of_seats")
    val noOfSeats: String,
    @SerializedName("operator_api_key")
    val operatorApiKey: String,
    @SerializedName("origin_id")
    val originId: String,
    @SerializedName("reservation_id")
    val reservationId: String,
)