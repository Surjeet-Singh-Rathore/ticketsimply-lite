package com.bitla.ts.presentation.view.merge_bus.pojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class MatchScore: Serializable {
    @SerializedName("seat_nos")
    @Expose
    var seatNos: String? = null

    @SerializedName("seat_type")
    @Expose
    var seatType: String? = null

    @SerializedName("seat_number_availability_percentage")
    @Expose
    var seatNumberAvailabilityPercentage: String? = null

    @SerializedName("seat_type_match_percentage")
    @Expose
    var seatTypeMatchPercentage: String? = null
}
