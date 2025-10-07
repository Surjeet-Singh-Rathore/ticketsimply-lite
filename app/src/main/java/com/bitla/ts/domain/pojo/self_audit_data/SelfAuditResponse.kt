package com.bitla.ts.domain.pojo.self_audit_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class SelfAuditResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("result")
    @Expose
    var result: Result? = null
}