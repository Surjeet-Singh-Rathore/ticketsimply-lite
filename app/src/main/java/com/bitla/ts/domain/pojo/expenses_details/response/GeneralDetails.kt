package com.bitla.ts.domain.pojo.expenses_details.response

import com.google.gson.annotations.SerializedName

data class GeneralDetails(
    @SerializedName("key")
    val key: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("value")
    var value: String
)