package com.example.buscoach.service_details_response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CoachDetails: Serializable {

    @SerializedName("no_of_rows")
    var noOfRows: Int? = null

    @SerializedName("no_of_cols")
    var noOfCols: Int? = null

    @SerializedName("total_seats")
    var totalSeats: Int? = null

    @SerializedName("available_seats")
    var availableSeats: Int? = null

    @SerializedName("booked_seats_by_user")
    var bookedSeatsByUser: String? = null

    @SerializedName("driver_position")
    var driverPosition: String? = null

    @SerializedName("coach_number")
    var coachNumber: Any? = null

    @SerializedName("seat_details")
    var seatDetails: List<SeatDetail>? = null

}
