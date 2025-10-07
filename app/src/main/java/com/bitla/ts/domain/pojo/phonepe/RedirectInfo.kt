package com.bitla.ts.domain.pojo.phonepe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RedirectInfo {

    @SerializedName("url")
    @Expose
    var url: String = ""

    @SerializedName("method")
    @Expose
    var method: String? = null



}