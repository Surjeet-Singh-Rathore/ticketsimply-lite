package com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PagenationData {

    @SerializedName("position")
    @Expose
    var position : Int = 0

    @SerializedName("is_selected")
    @Expose
    var isSelected: Boolean = false


}