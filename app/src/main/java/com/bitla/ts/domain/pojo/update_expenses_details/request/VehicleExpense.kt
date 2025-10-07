package com.bitla.ts.domain.pojo.update_expenses_details.request


import com.google.gson.annotations.SerializedName

data class VehicleExpense(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("remarks")
    var remarks: String? = null
)