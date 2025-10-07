package com.bitla.ts.domain.pojo.destination_pair

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {
    @SerializedName("origin")
    @Expose
    var origin: Origin? = null

    @SerializedName("destination")
    @Expose
    var destination: Destination? = null

}
