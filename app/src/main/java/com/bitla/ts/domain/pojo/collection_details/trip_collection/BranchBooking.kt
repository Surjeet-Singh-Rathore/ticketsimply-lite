package com.bitla.ts.domain.pojo.collection_details.trip_collection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class BranchBooking {

    @SerializedName("branch_name")
    @Expose
    var branchName: String? = null

    @SerializedName("total_amount")
    @Expose
    var totalAmount: String? = null

    @SerializedName("total_seats")
    @Expose
    var totalSeats: Int? = null

    @SerializedName("passenger_details")
    @Expose
    var passengerDetails: ArrayList<PassengerDetailData>? = null
}