package com.bitla.ts.domain.pojo.book_with_extra_seat.request

import com.google.gson.annotations.SerializedName

data class SeatDetail(
    @SerializedName("additional_fare")
    val additionalFare: String,
    @SerializedName("age")
    val age: String,
    @SerializedName("discount_amount")
    val discountAmount: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id_card_number")
    val idCardNumber: String,
    @SerializedName("id_card_type")
    val idCardType: Int,
    @SerializedName("is_primary")
    val isPrimary: Boolean,
    @SerializedName("is_round_trip_seat")
    val isRoundTripSeat: Boolean,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("name")
    val name: Any,
    @SerializedName("nationality")
    val nationality: String,
    @SerializedName("passenger_category")
    val passengerCategory: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("fare")
    var fare: Any? = null,

)