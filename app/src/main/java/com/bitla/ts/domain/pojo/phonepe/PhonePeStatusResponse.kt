package com.bitla.ts.domain.pojo.phonepe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhonePeStatusResponse {
    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("reprint_count")
    @Expose
    val reprint_count: Int = 0

    @SerializedName("is_update_print_count")
    @Expose
    val is_update_print_count: String = "false"

    @SerializedName("print_count")
    @Expose
    val print_count: Int = 0

    @SerializedName("status")
    @Expose
    val status: Int = 0

}