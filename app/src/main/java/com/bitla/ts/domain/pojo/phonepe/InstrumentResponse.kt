package com.bitla.ts.domain.pojo.phonepe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InstrumentResponse {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("redirectInfo")
    @Expose
    var redirectInfo: RedirectInfo? = null

}