package com.bitla.ts.domain.pojo.cancellation_details_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_from_bus_opt_app")
    val isFromBusOptApp: Boolean,
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("operator_api_key")
    val operatorApiKey: String,
    @SerializedName("cancel_type")
    val cancelType: String,
    @SerializedName("ticket_cancellation_percentage_p")
    val ticketCancellationPercentageP: String,
    @SerializedName("passenger_details")
    val passengerDetails: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("response_format")
    val responseFormat: String,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("zero_percent")
    val zeroPercent: Boolean,
    val json_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    @SerializedName("is_bima_ticket")
    var isBimaTicket: Boolean? = false
)