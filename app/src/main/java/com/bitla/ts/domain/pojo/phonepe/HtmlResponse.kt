package com.bitla.ts.domain.pojo.phonepe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class HtmlResponse : Serializable {
    @SerializedName("html_body")
    @Expose
    var htmlBody: String? = null
}