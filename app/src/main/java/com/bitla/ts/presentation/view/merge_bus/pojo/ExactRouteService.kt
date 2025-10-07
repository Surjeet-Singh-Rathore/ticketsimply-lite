package com.bitla.ts.presentation.view.merge_bus.pojo

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ExactRouteService: Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null

    @SerializedName("dep_time")
    @Expose
    var depTime: String? = null

    @SerializedName("arr_time")
    @Expose
    var arrTime: String? = null

    @SerializedName("origin")
    @Expose
    var origin: String? = null

    @SerializedName("destination")
    @Expose
    var destination: String? = null

    @SerializedName("available_seats")
    @Expose
    var availableSeats: Int? = null

    @SerializedName("total_seats")
    @Expose
    var totalSeats: Int? = null

    @SerializedName("bus_type")
    @Expose
    var busType: String? = null

    @SerializedName("dep_date")
    @Expose
    var depDate: String? = null

    @SerializedName("arr_date")
    @Expose
    var arrDate: String? = null

    @SerializedName("net_revenue")
    @Expose
    var netRevenue: String? = null

    @SerializedName("match_score")
    @Expose
    var matchScore: MatchScore? = null
}
