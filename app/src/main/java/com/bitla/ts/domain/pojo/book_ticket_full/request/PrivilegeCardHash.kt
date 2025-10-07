package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PrivilegeCardHash {
    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("card_number")
    @Expose
    var cardNumber: String? = null

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null

    @SerializedName("selected_seats")
    @Expose
    var selectedSeats: String? = null
}