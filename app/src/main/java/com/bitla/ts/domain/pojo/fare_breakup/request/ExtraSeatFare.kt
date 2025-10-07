package com.bitla.ts.domain.pojo.fare_breakup.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExtraSeatFare {
    @SerializedName("seat_no")
    @Expose
    var seatNo: String? = null

    @SerializedName("fare")
    @Expose
    var fare: String? = null
}