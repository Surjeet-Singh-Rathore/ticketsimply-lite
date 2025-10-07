package com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response


import com.google.gson.annotations.SerializedName

data class BoardingStage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)