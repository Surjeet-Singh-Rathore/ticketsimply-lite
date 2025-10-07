package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultUpdateRoute {

    @SerializedName("code")
    @Expose
    var code: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""
}