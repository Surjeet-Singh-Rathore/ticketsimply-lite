package com.bitla.ts.domain.pojo.service_details_response


import com.google.gson.annotations.SerializedName

class ServiceDetailsModel {

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("body")
    var body: Body = Body()

    @SerializedName("message")
    var message: String? = null
}
