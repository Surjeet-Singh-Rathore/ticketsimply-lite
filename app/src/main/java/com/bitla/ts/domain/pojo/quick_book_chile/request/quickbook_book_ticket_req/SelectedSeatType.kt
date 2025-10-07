package com.bitla.ts.domain.pojo.quick_book_chile.request.quickbook_book_ticket_req


import com.google.gson.annotations.SerializedName

data class SelectedSeatType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("types")
    val types: MutableList<Type>?=null
)