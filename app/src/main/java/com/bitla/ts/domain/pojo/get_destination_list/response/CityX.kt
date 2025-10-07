package com.bitla.ts.domain.pojo.get_destination_list.response


import com.google.gson.annotations.SerializedName

data class CityX(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)