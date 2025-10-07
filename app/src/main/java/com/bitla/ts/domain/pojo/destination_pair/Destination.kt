package com.bitla.ts.domain.pojo.destination_pair

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Destination {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

}
