package com.bitla.ts.domain.pojo.preview_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PreviewRouteResponse {

    @SerializedName("code")
    @Expose
    val code: String = ""

    @SerializedName("result")
    @Expose
    val result: PreviewRouteResponseData ?= null


}