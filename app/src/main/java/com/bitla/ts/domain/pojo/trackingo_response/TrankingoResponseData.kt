package com.bitla.ts.domain.pojo.trackingo_response

import com.google.gson.annotations.SerializedName

data class TrankingoResponseData(
    @SerializedName("average_speed")
    val averageSpeed: String?,

    @SerializedName("maximum_speed")
    val maximumSpeed: String?,

    @SerializedName("passenger_count")
    val passengerCount: String?,

    @SerializedName("source")
    val source: String?,

    @SerializedName("destination")
    val destination: String?,

    @SerializedName("driver_name")
    val driverName: String?,

    @SerializedName("driver2_name")
    val driver2Name: String?,

    @SerializedName("cleaner_name")
    val cleanerName: String?,

    @SerializedName("odometer")
    val odometer: String?,

    @SerializedName("distance")
    val distance: String?,

    @SerializedName("at_last_stop")
    val atLastStop: String?,

    @SerializedName("total_stopped")
    val totalStopped: String?,

    @SerializedName("service_trip_id")
    val serviceTripId: String?,

    @SerializedName("alert_places")
    val alertPlaces: String?,

    @SerializedName("feed_trackers")
    val feedTrackers: List<Any>, // Replace Any with actual type if known

    @SerializedName("trip_status")
    val tripStatus: String?,

//    @SerializedName("service_place")
//    val servicePlace: ServicePlace?,

    @SerializedName("asset_id")
    val assetId: Int,

    @SerializedName("asset_number")
    val assetNumber: String?,

    @SerializedName("asset_name")
    val assetName: String?,

    @SerializedName("last_located_coordinates")
    val lastLocatedCoordinates: List<Double>?,

    @SerializedName("last_located_at")
    val lastLocatedAt: String?,

    @SerializedName("current_address")
    val currentAddress: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("speed")
    val speed: Int?,

    @SerializedName("gps_timestamp")
    val gpsTimestamp: String?
)
