package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class MostSearched(
    @SerializedName("created_date")
    val createdDate: String?,
    @SerializedName("dest_id")
    val destId: String?,
    @SerializedName("dest_name")
    val destName: String?,
    @SerializedName("origin_id")
    val originId: String?,
    @SerializedName("origin_name")
    val originName: String?,
    @SerializedName("updated_date")
    val updatedDate: String?
)