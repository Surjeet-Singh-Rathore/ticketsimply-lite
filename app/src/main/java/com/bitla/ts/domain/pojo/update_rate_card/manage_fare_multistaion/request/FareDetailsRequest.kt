package com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FareDetailsRequest : Serializable {
    @SerializedName("seat_type_id")
    var seat_type_id: String? = null

    @SerializedName("seat_type")
    var seat_type: String? = null

    @SerializedName("fare")
    var fare: String? = null
}