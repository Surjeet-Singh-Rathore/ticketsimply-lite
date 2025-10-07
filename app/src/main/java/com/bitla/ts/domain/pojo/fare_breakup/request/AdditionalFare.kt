package com.bitla.ts.domain.pojo.fare_breakup.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdditionalFare {
    @SerializedName("seat_no")
    @Expose
    var seatNo: String? = null

    @SerializedName("fare")
    @Expose
    var fare: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("passenger_category")
    @Expose
    var passengerCategory: String? = null
}