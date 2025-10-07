package com.bitla.ts.domain.pojo.create_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ScheduleData {

    @SerializedName("departure_time")
    @Expose
    var departureTime: String = ""

    @SerializedName("arrival_time")
    @Expose
    var arrivalTime: String = ""

    @SerializedName("duration")
    @Expose
    var duration: String = ""

    @SerializedName("from_date")
    @Expose
    var fromDate: String = ""

    @SerializedName("to_date")
    @Expose
    var toDate: String = ""

    @SerializedName("days")
    @Expose
    var days: String = ""

    @SerializedName("advance_booking")
    @Expose
    var advanceBooking: String = ""

    @SerializedName("alternate_day_service")
    @Expose
    var alternateDayService: String = ""

}