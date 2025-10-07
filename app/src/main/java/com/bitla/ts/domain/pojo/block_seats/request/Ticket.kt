package com.bitla.ts.domain.pojo.block_seats.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Ticket {

    @SerializedName("selected_seats")
    @Expose
    var selectedSeats: String? = null

}
