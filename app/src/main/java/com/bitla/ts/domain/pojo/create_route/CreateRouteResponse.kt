package com.bitla.ts.domain.pojo.create_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateRouteResponse {

    @SerializedName("code")
    @Expose
    var code: Int ?= null

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("id")
    @Expose
    var id: Int ?= null

    @SerializedName("seat_types")
    @Expose
    var seatType: String = ""

    @SerializedName("is_ac_coach")
    @Expose
    var isAcCoach: Boolean = false
}