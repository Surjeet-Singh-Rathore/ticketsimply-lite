package com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("cancel_type")
    val cancelType: String,
    @SerializedName("is_from_bus_opt_app")
    val isFromBusOptApp: Boolean,
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("operator_api_key")
    val operatorApiKey: String,
    @SerializedName("passenger_details")
    val passengerDetails: String,
    @SerializedName("response_format")
    val responseFormat: String,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("ticket_cancellation_percentage_p")
    val ticketCancellationPercentageP: String,
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("travel_date")
    val travelDate: String,
    @SerializedName("zero_percent")
    val zeroPercent: Boolean,
    @SerializedName("is_onbehalf_booked_user")
    val isOnbehalfBookedUser: Boolean,
    @SerializedName("onbehalf_online_agent_flag")
    val onbehalf_online_agent_flag: Boolean,
    @SerializedName("on_behalf_user_id")
    val onBehalfUserId: Int?,
    val json_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    @SerializedName("is_bima_ticket")
    var isBimaTicket: Boolean? = false
)