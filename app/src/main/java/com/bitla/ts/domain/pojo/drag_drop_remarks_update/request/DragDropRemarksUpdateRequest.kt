package com.bitla.ts.domain.pojo.drag_drop_remarks_update.request


import com.google.gson.annotations.SerializedName

data class DragDropRemarksUpdateRequest(
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("pnr_number")
    val pnrNumber: String?,
    @SerializedName("remarks")
    val remarks: String?
)