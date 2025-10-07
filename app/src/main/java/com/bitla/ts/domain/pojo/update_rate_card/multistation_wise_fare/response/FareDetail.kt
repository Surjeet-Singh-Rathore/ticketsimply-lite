package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FareDetail(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("seat_type")
    var seatType: String? = null,

    @SerializedName("fare")
    @Expose
    var fare: String? = null,

    @SerializedName("edited_fare")
    @Expose
    var editedFare: String? = null

) : Serializable