package com.bitla.mba.morningstartravels.mst.pojo.booking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Transaction : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("pnr_number")
    @Expose
    var pnrNumber: String? = null

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null

    @SerializedName("origin_id")
    @Expose
    var originId: Int? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: Int? = null

    @SerializedName("transaction_status")
    @Expose
    var transactionStatus: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("boarding_point")
    @Expose
    var boardingPoint: String? = null

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("landmark")
    @Expose
    var landmark: String? = null

    @SerializedName("seat_numbers")
    @Expose
    var seatNumbers: String? = null

    @SerializedName("service_number")
    @Expose
    var serviceNumber: String? = null

    @SerializedName("origin_name")
    @Expose
    var originName: String? = null

    @SerializedName("destination_name")
    @Expose
    var destinationName: String? = null

    @SerializedName("wallet_booking")
    @Expose
    var walletBooking: Boolean? = null

    @SerializedName("is_e_phone_booking")
    @Expose
    var isEPhoneBooking: Boolean? = null

    @SerializedName("is_feedback_submitted")
    @Expose
    var isFeedbackSubmitted: Boolean? = null

    @SerializedName("is_in_journey")
    @Expose
    var isInJourney: Boolean? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

}
