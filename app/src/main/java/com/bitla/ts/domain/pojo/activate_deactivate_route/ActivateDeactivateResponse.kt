package com.bitla.ts.domain.pojo.activate_deactivate_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ActivateDeactivateResponse {

    @SerializedName("code")
    @Expose
    var code: Int = 0

    @SerializedName("result")
    @Expose
    var result: Message ?= null

    @SerializedName("message")
    @Expose
    var message: String = ""
}