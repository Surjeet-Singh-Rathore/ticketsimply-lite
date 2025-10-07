package com.bitla.ts.domain.pojo.passenger_details_result

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("additional_fare")
    val additionalFare: Int,
    @SerializedName("age")
    var age: String,
    @SerializedName("discount_amount")
    val discountAmount: Int,
    @SerializedName("fare")
    val fare: Int,
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
    var name: String,
    @SerializedName("nationality")
    val nationality: String,
    @SerializedName("passenger_category")
    val passengerCategory: String,
    @SerializedName("passport_expiry_date")
    val passportExpiryDate: String,
    @SerializedName("passport_issued_date")
    val passportIssuedDate: String,
    @SerializedName("place_of_issue")
    val placeOfIssue: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("sex")
    val sex: String,
)