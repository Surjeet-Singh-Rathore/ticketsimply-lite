package com.bitla.ts.domain.pojo.privilege_details_model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PrivilegeRequestModel(

    @SerializedName("bcc_id")
    @Expose
    var bccId: String? = null,

    @SerializedName("format")
    @Expose
    var format: String? = null,

    @SerializedName("method_name")
    @Expose
    var methodName: String? = null,


    @SerializedName("req_body")
    @Expose
    val req_body: com.bitla.ts.domain.pojo.privilege_details_model.request.ReqBody
)