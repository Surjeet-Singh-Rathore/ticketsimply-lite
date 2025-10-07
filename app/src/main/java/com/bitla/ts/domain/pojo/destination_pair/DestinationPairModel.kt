package com.bitla.ts.domain.pojo.destination_pair

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DestinationPairModel(get: Any) {

    @SerializedName("result")
    @Expose
    var result: MutableList<Result>? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

}
