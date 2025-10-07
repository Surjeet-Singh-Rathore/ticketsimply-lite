package com.bitla.ts.domain.pojo.quick_book_chile.request.quickbook_book_ticket_req


import com.google.gson.annotations.SerializedName

data class Type(
    @SerializedName("id")
    val id: Int,
    @SerializedName("selected_seat_count")
    var selectedSeatCount: Int
)