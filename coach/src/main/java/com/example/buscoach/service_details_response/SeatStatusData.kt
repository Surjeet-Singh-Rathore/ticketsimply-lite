package com.example.buscoach.service_details_response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SeatStatusData: Serializable {

    @SerializedName("status")
    var status: Int? = 0

    @SerializedName("price")
    var value: String? = ""

    @SerializedName("filter_position")
    var position: Int? = 0
}