package com.bitla.ts.domain.pojo.get_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRouteResponse {

    @SerializedName("result")
    @Expose
    var result: GetRouteData ?= null

    @SerializedName("code")
    @Expose
    var code: Int ?= null
}