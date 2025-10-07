package com.bitla.ts.domain.pojo.preview_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StageData {
    @SerializedName("id")
    @Expose
    val id: String = ""

    @SerializedName("name")
    @Expose
    val name: String = ""

    @SerializedName("lat")
    @Expose
    val lat: String = ""

    @SerializedName("long")
    @Expose
    val long: String = ""
}