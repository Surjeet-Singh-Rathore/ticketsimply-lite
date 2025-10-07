package com.bitla.ts.domain.pojo.pay_pending_amount

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Result {
    @SerializedName("message")
    @Expose
    var message: String? = null
}