package com.bitla.ts.domain.pojo.employees_details.request


import com.google.gson.annotations.SerializedName

data class EmployeesDetailsRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)