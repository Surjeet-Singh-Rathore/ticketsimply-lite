package com.bitla.ts.domain.pojo.pinelabs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseData {
    @SerializedName("ResponseCode")
    @Expose
    var responseCode: Int? = null

    @SerializedName("AppVersion")
    @Expose
    var appVersion: String? = null

    @SerializedName("ParameterJson")
    @Expose
    var parameterJson: String? = null

    @SerializedName("ResponseMsg")
    @Expose
    var responseMsg: String? = null
}