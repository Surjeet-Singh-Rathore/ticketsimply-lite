package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class PassengerGstDetails {
    @SerializedName("registration_name")
    @Expose
    var registrationName: String? = null

    @SerializedName("gst_id")
    @Expose
    var gstId: String? = null
}