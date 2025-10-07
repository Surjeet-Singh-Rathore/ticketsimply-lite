package com.bitla.ts.domain.pojo.book_extra_seat.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ContactDetail {
    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("emergency_name")
    @Expose
    var emergencyName: String? = null

    @SerializedName("alternate_number")
    @Expose
    var alternateNumber: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null
}