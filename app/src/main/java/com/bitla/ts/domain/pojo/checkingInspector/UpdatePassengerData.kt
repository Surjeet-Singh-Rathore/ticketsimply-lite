package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class UpdatePassengerData(
    @SerializedName("pnr_no")
    var pnrNo: String = "",
    @SerializedName("seat_no")
    var seatNo: String="",
    @SerializedName("boarded_status")
    var boardedStatus: String = "NO",
    @SerializedName("gender")
    var gender: String = "",


)