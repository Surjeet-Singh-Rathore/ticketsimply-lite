package com.bitla.ts.domain.pojo.rapid_booking.request

import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val destination: String,
    val locale: String?,
    val no_of_seats: Int,
    val origin: String,
    val res_id: String,
    val seat_count: SeatCount,
    val mot_coupon: String?= null,
    val is_offline_booked_tic: Boolean?= false,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier:Boolean = true,
    @SerializedName("booking_details")
    @Expose
    var bookingDetails: BookingDetails? = null
)