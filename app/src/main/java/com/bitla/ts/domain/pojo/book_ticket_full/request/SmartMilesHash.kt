package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SmartMilesHash {
    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null
}