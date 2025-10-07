package com.bitla.ts.domain.pojo.collection_details.trip_collection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class PassengerDetailData {
    @SerializedName("pnr_number")
    @Expose
    var pnrNumber: String? = null

    @SerializedName("seat_number")
    @Expose
    var seatNumbers: String? = null

    @SerializedName("booked_by")
    @Expose
    var bookedBy: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null
    @SerializedName("booked_date_time")
    @Expose
    var bookedDateTime: String? = ""

    @SerializedName("from_to")
    @Expose
    var fromTo: String? = null
}