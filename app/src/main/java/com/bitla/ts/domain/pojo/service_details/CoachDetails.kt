package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoachDetails {
    @SerializedName("no_of_rows")
    @Expose
    var noOfRows: Int? = null

    @SerializedName("no_of_cols")
    @Expose
    var noOfCols: Int? = null

    @SerializedName("total_seats")
    @Expose
    var totalSeats: Int? = null

    @SerializedName("available_seats")
    @Expose
    var availableSeats: Int? = null

    @SerializedName("driver_position")
    @Expose
    var driverPosition: String? = null

    @SerializedName("coach_number")
    @Expose
    var coachNumber: Any? = null

    @SerializedName("seat_details")
    @Expose
    var seatDetails: List<SeatDetail>? = null

}