package com.bitla.ts.domain.pojo.book_with_extra_seat.request


import com.google.gson.annotations.SerializedName

data class ContactDetail(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("emergency_name")
    val emergencyName: String? = null,
    @SerializedName("mobile_number")
    val mobileNumber: String? = null,
    @SerializedName("alternate_number")
    val alternateNumber: String? = null,
    @SerializedName("send_sms_on_booking")
    val sendSmsOnBooking: Boolean
)