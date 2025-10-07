package com.bitla.ts.domain.pojo.booking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SeatStatusData {
    @SerializedName("status")
    @Expose
    var status: Int? = 0

    @SerializedName("price")
    @Expose
    var value: String? = ""

    @SerializedName("filter_position")
    @Expose
    var position: Int? = 0

    @SerializedName("seat_number")
    @Expose
    var seatNumber: String? = null

}