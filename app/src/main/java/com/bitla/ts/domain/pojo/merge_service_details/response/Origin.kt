package com.bitla.ts.domain.pojo.merge_service_details.response


import com.google.gson.annotations.SerializedName

data class Origin(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)