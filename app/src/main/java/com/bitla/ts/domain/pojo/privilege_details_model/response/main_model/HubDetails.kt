package com.bitla.ts.domain.pojo.privilege_details_model.response.main_model

import com.google.gson.annotations.SerializedName

data class HubDetails(
    @SerializedName(":id")
    val id: Int,
    @SerializedName(":label")
    val label: String? = null
)