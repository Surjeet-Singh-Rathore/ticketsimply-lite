package com.bitla.ts.domain.pojo.self_audit_data

import android.media.Rating
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Result {
    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("trip_id")
    @Expose
    var tripId: String? = null

    @SerializedName("operator_name")
    @Expose
    var operatorName: String? = null

    @SerializedName("route_name")
    @Expose
    var routeName: String? = null

    @SerializedName("option_questions")
    @Expose
    var optionQuestions: List<OptionQuestion>? = null

    @SerializedName("rating")
    @Expose
    var rating: Rating? = null

    @SerializedName("boarding_data")
    @Expose
    var boardingData: List<BoardingData>? = null
}