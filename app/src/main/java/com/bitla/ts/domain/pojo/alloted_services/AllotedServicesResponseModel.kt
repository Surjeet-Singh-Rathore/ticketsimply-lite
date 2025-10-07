package com.bitla.ts.domain.pojo.alloted_services


import com.google.gson.annotations.SerializedName

data class AllotedServicesResponseModel(
    @SerializedName("code")
    var code: Int?=null,
    @SerializedName("header")
    var header: String,
    @SerializedName("services")
    var services: List<Service>,
    @SerializedName("view_summary")
    var viewSummary: ViewSummary,
    @SerializedName("resp_hash")
    var respHash: ArrayList<RespHash>,
    var result: Result?

)

class Result(
    val message: String?
)