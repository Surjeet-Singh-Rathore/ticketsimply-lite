package com.bitla.ts.domain.pojo.book_extra_seat.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SeatDetail {
    @SerializedName("seat_number")
    @Expose
    var seatNumber: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("sex")
    @Expose
    var sex: String? = null

    @SerializedName("id_card_type")
    @Expose
    var idCardType: String? = null

    @SerializedName("id_card_number")
    @Expose
    var idCardNumber: String? = null

    @SerializedName("nationality")
    @Expose
    var nationality: String? = null

    @SerializedName("fare")
    @Expose
    var fare: String? = null

    @SerializedName("is_primary")
    @Expose
    var isPrimary: String? = null

    @SerializedName("DOB")
    @Expose
    var dob: String? = null
}