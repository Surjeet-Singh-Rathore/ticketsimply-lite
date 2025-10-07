package com.bitla.ts.domain.pojo.paytm_pos_integration.paytm_pos_txn_status_api.request


import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName


data class PaytmPosTxnStatusRequest(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("connecting_pnr_number")
    val connectingPnrNumber: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("is_extra_seat")
    val isExtraSeat: Boolean,
    @SerializedName("is_round_trip")
    val isRoundTrip: Boolean,
    @SerializedName("is_send_sms")
    val isSendSms: Boolean,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("paytm_pos_payment_type")
    val paytmPosPaymentType: Int,
    @SerializedName("paytm_pos_response")
    val paytmPosResponse: JsonObject,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("reservation_id")
    val reservationId: String
)