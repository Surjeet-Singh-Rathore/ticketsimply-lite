package com.bitla.ts.domain.pojo.modify_route

import com.bitla.ts.domain.pojo.view_reservation.Result
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModifyRouteResponse {

    @SerializedName("code")
    @Expose
    val code: Int = 0

    @SerializedName("result")
    @Expose
    val result: Result? = null

    @SerializedName("message")
    @Expose
    val message: String = ""
}