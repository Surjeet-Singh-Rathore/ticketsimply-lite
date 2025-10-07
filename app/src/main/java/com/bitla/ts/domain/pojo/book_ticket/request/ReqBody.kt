package com.bitla.ts.domain.pojo.book_ticket.request

import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val destination_id: String,
    val mot_coupon: String? = null,
    val is_from_bus_opt_app: String,
    val is_rapid_booking: String,
    val locale: String?,
    val no_of_seats: String,
    val operator_api_key: String,
    val origin_id: String,
    val reservation_id: String,
    val boarding_at: String,
    val drop_off: String,
    val is_offline_booked_tic:Boolean = false,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    val response_format: String,
    @SerializedName("booking_details")
    @Expose
    var bookingDetails: BookingDetails? = null

)