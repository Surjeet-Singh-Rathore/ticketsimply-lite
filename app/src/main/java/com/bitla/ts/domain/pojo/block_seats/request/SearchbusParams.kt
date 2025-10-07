package com.bitla.ts.domain.pojo.block_seats.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchbusParams {

    @SerializedName("from")
    @Expose
    var from: Any? = null

    @SerializedName("to")
    @Expose
    var to: Any? = null

}
