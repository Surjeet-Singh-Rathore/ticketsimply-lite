package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FareDetailsResponse : Serializable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("seat_type")
    var seat_type: String? = null

    @SerializedName("fare")
    @Expose
    var fare: String? = null
}