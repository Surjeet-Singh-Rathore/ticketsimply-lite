package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ContactDetail {
    @SerializedName("emergency_name")
    @Expose
    var emergencyName: String? = null

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("alternate_number")
    @Expose
    var alternateNumber: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("send_sms_on_booking")
    @Expose
    var sendSmsOnBooking: Boolean? = null
}