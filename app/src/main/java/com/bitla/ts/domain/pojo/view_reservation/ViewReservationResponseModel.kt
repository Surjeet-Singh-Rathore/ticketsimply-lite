package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class ViewReservationResponseModel(
    @SerializedName("booked_seats")
    var bookedSeats: BookedSeats,
    @SerializedName("booking_details")
    var bookingDetails: BookingDetails,
    @SerializedName("chart_type")
    var chartType: List<ChartType>,
    @SerializedName("city_hash")
    var cityHash: CityHash,
    @SerializedName("city_pairs")
    var cityPairs: List<List<String>>,
    @SerializedName("city_seq_order")
    var citySeqOrder: ArrayList<CitySeqOrder>,
    @SerializedName("coach_number")
    var coachNumber: String?,
    @SerializedName("code")
    var code: Int,
    @SerializedName("crew_temparature")
    var crewTemparature: Any?,
    @SerializedName("dep_time")
    var depTime: String,
    @SerializedName("destination_id")
    var destinationId: Int,
    @SerializedName("destination_name")
    var destinationName: String,
    @SerializedName("header")
    var header: String,
    @SerializedName("is_apply_bp_dp_fare")
    var isApplyBpDpFare: Boolean,
    @SerializedName("multi_station_booking_details")
    var multiStationBookingDetails: List<MultiStationBookingDetail>,
    @SerializedName("name")
    var name: String,
    @SerializedName("number")
    var number: String,
    @SerializedName("origin_id")
    var originId: Int,
    @SerializedName("origin_name")
    var originName: String,
    @SerializedName("res_id")
    var resId: String,
    @SerializedName("resp_hash")
    var respHash: ArrayList<RespHash>,
    @SerializedName("travel_date")
    var travelDate: String,
    @SerializedName("vehicle_document")
    var vehicleDocument: List<Any>,
    @SerializedName("passenger_details")
    var passengerDetails: ArrayList<PassengerDetail>,
    @SerializedName("can_alter_cancellation_percentage")
    var canAlterCancellationPercentage: Boolean?,

    @SerializedName("result")
    var result: Result?,
    @SerializedName("message")
    var message: String,

    @SerializedName("boarded")
    var boarded: String? = null,
    @SerializedName("yet_to_board")
    var yetToBoard: String? = null,
    @SerializedName("booked")
    var booked: String? = null,
    @SerializedName("pnr_group")
    var pnr_group: ArrayList<PnrGroup?>? = null,
    @SerializedName("is_trip_complete")
    var isTripComplete: String?= null,

    @SerializedName("city_name")
    var cityName: String? = "",
)