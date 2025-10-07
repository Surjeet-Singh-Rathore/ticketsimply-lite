package com.example.buscoach.service_details_response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LegendDetail: Serializable {
    @SerializedName("color_legend")
    @Expose
    var colorLegend: Any? = null

    @SerializedName("color")
    @Expose
    var color: String? = null
}