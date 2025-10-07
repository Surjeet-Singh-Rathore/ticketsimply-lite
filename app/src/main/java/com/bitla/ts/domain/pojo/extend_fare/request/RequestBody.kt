package com.bitla.ts.domain.pojo.extend_fare.request

import com.google.gson.annotations.SerializedName

class RequestBody {
    @SerializedName("api_key")
    var api_key: String? = null

    @SerializedName("auth_pin")
    var auth_pin : String? = null

    @SerializedName("extend_fare")
    var extend_fare: RequestBodyExtendFarePojo? = null

    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true

    @SerializedName("locale")
    var locale: String? = ""
}