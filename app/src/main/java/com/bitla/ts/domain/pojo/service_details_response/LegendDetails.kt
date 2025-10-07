package com.bitla.ts.domain.pojo.service_details_response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LegendDetail {
    @SerializedName("color_legend")
    @Expose
    var colorLegend: Any? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("branch_color_legend")
    @Expose
    var branchColorLegend: String? = null
}