package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class PASSENGERALTERNATENO(
    @SerializedName("Individual")
    val individual: String,
    @SerializedName("Option")
    val option: String
)