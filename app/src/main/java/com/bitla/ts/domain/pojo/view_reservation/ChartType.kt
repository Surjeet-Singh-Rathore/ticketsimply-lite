package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class ChartType(
    @SerializedName("icon")
    var icon: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("is_selected")
    var isSelected: Boolean,
    @SerializedName("label")
    var label: String
)