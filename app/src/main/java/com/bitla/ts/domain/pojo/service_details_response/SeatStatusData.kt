package com.bitla.ts.domain.pojo.service_details_response

import com.google.gson.annotations.SerializedName

class SeatStatusData {

    @SerializedName("status")
    var status: Int? = 0

    @SerializedName("price")
    var value: String? = ""

    @SerializedName("filter_position")
    var position: Int? = 0
}