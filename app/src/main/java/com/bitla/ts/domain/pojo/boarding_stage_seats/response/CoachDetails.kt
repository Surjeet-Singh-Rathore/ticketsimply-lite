package com.bitla.ts.domain.pojo.boarding_stage_seats.response


import com.google.gson.annotations.SerializedName

data class CoachDetails(
    @SerializedName("available_seats")
    val availableSeats: Int?,
    @SerializedName("coach_number")
    val coachNumber: Any?,
    @SerializedName("driver_position")
    val driverPosition: String?,
    @SerializedName("is_seat_type_category_present")
    val isSeatTypeCategoryPresent: Boolean?,
    @SerializedName("no_of_cols")
    val noOfCols: Int?,
    @SerializedName("no_of_rows")
    val noOfRows: Int?,
    @SerializedName("seat_details")
    val seatDetails: List<SeatDetail?>?,
    @SerializedName("total_seats")
    val totalSeats: Int?
)