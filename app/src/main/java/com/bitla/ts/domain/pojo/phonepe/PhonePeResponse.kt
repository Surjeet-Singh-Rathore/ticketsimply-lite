package com.bitla.ts.domain.pojo.phonepe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhonePeResponse {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null
}