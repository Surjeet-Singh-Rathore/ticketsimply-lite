package com.bitla.ts.domain.pojo.route_manager

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CitiesListResponse {

    @SerializedName("result")
    @Expose
    var result: ArrayList<CitiesListData> = arrayListOf()

    @SerializedName("code")
    @Expose
    var code: Int ?= null

    @SerializedName("message")
    @Expose
    var message: String? = null
}
