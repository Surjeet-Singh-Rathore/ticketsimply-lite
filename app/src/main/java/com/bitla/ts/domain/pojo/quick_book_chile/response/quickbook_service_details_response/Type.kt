package com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response


import com.google.gson.annotations.SerializedName

data class Type(
    @SerializedName("fare")
    val fare: Double? = null,
    @SerializedName("id")
    val idType: Int? = null,
    @SerializedName("label")
    val label: String? = null
)