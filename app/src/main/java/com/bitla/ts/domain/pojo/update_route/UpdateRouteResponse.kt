package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateRouteResponse {

    @SerializedName("code")
    @Expose
    var code: Int = 0

    @SerializedName("result")
    @Expose
    var result: ResultUpdateRoute ?= null

    @SerializedName("message")
    @Expose
    var message: String = ""
}