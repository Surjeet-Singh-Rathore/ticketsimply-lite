package com.bitla.ts.domain.pojo.service_details_response


import com.google.gson.annotations.SerializedName


class BookingTypeSeatCounts {

    @SerializedName("total_seats_count")
    var totalSeatsCount: Int? = null

    @SerializedName("available_seats_count")
    var availableSeatsCount: Int? = null

    @SerializedName("booked_seats_count")
    var bookedSeatsCount: Int? = null

    @SerializedName("phone_booking_count")
    var phoneBookingCount: Int? = null

    @SerializedName("quota_blocked_count")
    var quotaBlockedCount: Int? = null

}
