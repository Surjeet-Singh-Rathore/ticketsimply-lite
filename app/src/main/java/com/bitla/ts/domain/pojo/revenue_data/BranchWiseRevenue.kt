package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class BranchWiseRevenue {
    @SerializedName("branch_name")
    @Expose
    var branchName: String? = null

    @SerializedName("booked_seats")
    @Expose
    var bookedSeats: Int? = null

    @SerializedName("fare")
    @Expose
    var fare: Float? = null

    @SerializedName("cancellation")
    @Expose
    var cancellation: Float? = null
}