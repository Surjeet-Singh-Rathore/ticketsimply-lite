package com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("types")
    val types: MutableList<Type>,
    var currentCount: Int = 0
)