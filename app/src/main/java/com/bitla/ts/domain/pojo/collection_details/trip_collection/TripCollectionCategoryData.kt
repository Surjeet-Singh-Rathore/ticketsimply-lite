package com.bitla.ts.domain.pojo.collection_details.trip_collection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class TripCollectionCategoryData {


    var categoryName: String = ""


    @SerializedName("passenger_details")
    @Expose
    var collectionDetails: ArrayList<BranchBooking>? = arrayListOf()
}