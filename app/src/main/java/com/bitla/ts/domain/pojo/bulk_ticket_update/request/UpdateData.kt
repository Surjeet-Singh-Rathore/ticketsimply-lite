package com.bitla.ts.domain.pojo.bulk_ticket_update.request

import com.google.gson.annotations.SerializedName

data class UpdateData(

    @SerializedName("is_single_seat")
    val isSingleSeat: String? = "",

    @SerializedName("phone_number")
    val phoneNumber: String? = "",

    @SerializedName("pass_name")
    val passName: String? = "",

    @SerializedName("pass_age")
    val passAge: String? = "",

    @SerializedName("email")
    val email: String? = "",

    @SerializedName("pass_gender")
    val passGender: String? = "",

    @SerializedName("boarding_at")
    val boardingAt: String? = "",

    @SerializedName("drop_off")
    val dropOff: String? = "",

    @SerializedName("country_code")
    val countryCode: String? = "",

    @SerializedName("updated_fare")
    val updatedFare: String? = "",
)