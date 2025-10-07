package com.bitla.ts.domain.pojo.booking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Tabs {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("icon")
    @Expose
    var icon: Int? = null

    @SerializedName("selected_point")
    @Expose
    var selectedPoint: String? = null
}