package com.bitla.ts.domain.pojo.pinelabs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CardSaleResponse {
    @SerializedName("Response")
    @Expose
    var response: ResponseData? = null

    @SerializedName("Detail")
    @Expose
    var detail: DetailData? = null

    @SerializedName("Header")
    @Expose
    var header: HeaderData? = null
}