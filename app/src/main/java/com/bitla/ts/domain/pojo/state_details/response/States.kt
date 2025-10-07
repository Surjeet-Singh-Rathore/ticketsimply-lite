package com.bitla.ts.domain.pojo.state_details.response


import com.google.gson.annotations.SerializedName

data class States(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)