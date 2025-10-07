package com.bitla.ts.domain.pojo.drag_drop_remarks_update.response


import com.google.gson.annotations.SerializedName

data class DragDropRemarksUpdateResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?
)