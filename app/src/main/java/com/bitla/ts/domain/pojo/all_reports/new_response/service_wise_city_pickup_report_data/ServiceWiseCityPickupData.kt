package com.bitla.ts.domain.pojo.all_reports.new_response.service_wise_city_pickup_report_data

import com.google.gson.annotations.SerializedName

data class ServiceWiseCityPickupData(
    @SerializedName("service_no")
    val serviceNo: String,

    @SerializedName("origin")
    val origin: String,

    @SerializedName("destination")
    val destination: String,

    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("vehicle_no")
    val vehicleNumber: String,

    @SerializedName("driver1")
    val driver1: String,

    @SerializedName("driver2")
    val driver2: String,

    @SerializedName("total_seats_booked")
    val totalSeatsBooked: String,

    @SerializedName("boarded")
    val boarded: Int,

    @SerializedName("yet_to_board")
    val yetToBoard: String,

    @SerializedName("un_boarded")
    val unBoarded: String,

    @SerializedName("no_show")
    val noShow: String,

    @SerializedName("missing")
    val missing: String,

    @SerializedName("available_seats")
    val availableSeats: String,

    @SerializedName("total_number_of_customer_travelling")
    val totalNumberOfCustomerTravelling: Int,

    @SerializedName("city_pickup_closed_by")
    val cityPickupClosedBy: String,

    @SerializedName("message")
    val message: String? = null
)
