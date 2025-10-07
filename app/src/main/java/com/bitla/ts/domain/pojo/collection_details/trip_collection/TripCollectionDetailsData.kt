package com.bitla.ts.domain.pojo.collection_details.trip_collection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class TripCollectionDetailsData {

    @SerializedName("branch_booking")
    @Expose
    var branchBooking: ArrayList<BranchBooking>? = null

    @SerializedName("online_agent_booking")
    @Expose
    var onlineAgentBooking: ArrayList<BranchBooking>? = null

    @SerializedName("api_agent_booking")
    @Expose
    var apiAgentBooking: ArrayList<BranchBooking>? = null

    @SerializedName("ebooking")
    @Expose
    var ebooking: ArrayList<BranchBooking>? = null

    @SerializedName("offline_branch_booking")
    @Expose
    var offlineBranchBooking: ArrayList<BranchBooking>? = null

    @SerializedName("offline_agent_booking")
    @Expose
    var offlineAgentBooking: ArrayList<BranchBooking>? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null
}