package com.bitla.ts.domain.pojo.block_seats.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlockSeatRequest {

    @SerializedName("bcc_id")
    @Expose
    var bccId: String? = null

    @SerializedName("method_name")
    @Expose
    var methodName: String? = null

    @SerializedName("format")
    @Expose
    var format: String? = null

    @SerializedName("req_body")
    @Expose
    var reqBody: ReqBody__1? = null

}
