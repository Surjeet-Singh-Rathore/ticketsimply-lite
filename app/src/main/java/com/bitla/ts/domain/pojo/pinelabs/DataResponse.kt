package com.bitla.ts.domain.pojo.pinelabs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataResponse {
    @SerializedName("ticket_number")
    @Expose
    var ticketNumber: String? = null

    @SerializedName("origin")
    @Expose
    var origin: String? = null

    @SerializedName("destination")
    @Expose
    var destination: String? = null

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null

    @SerializedName("service_number")
    @Expose
    var serviceNumber: String? = null

    @SerializedName("bus_type")
    @Expose
    var busType: String? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: Int? = null

    @SerializedName("seat_numbers")
    @Expose
    var seatNumbers: String? = null

    @SerializedName("dep_time")
    @Expose
    var depTime: String? = null

    @SerializedName("duration")
    @Expose
    var duration: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null
}
