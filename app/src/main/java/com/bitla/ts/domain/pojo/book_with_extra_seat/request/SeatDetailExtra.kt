package com.bitla.ts.domain.pojo.book_with_extra_seat.request


import com.google.gson.annotations.SerializedName

data class SeatDetailExtra(
    @SerializedName("age")
    val age: String,
    @SerializedName("alternate_number")
    val alternateNumber: String,
    @SerializedName("is_primary")
    val isPrimary: String,
    @SerializedName("mobile_number")
    val mobileNumber: String,
    @SerializedName("name")
    val name: String,
//    @SerializedName("remarks")
//    val remarks: String
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("fare")
    var fare: Any? = null,
    @SerializedName("id_card_number")
    val idCardNumber: String,
    @SerializedName("id_card_type")
    val idCardType: Int,
    @SerializedName("nationality")
    val nationality: String
)