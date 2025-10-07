package com.bitla.ts.domain.pojo.phonepe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("merchantId")
    @Expose
     var merchantId: String? = null

    @SerializedName("merchantTransactionId")
    @Expose
     var merchantTransactionId: String? = null

    @SerializedName("instrumentResponse")
    @Expose
     var instrumentResponse: InstrumentResponse? = null

}