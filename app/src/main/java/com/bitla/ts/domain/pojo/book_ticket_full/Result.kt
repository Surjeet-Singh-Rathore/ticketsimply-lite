package com.bitla.ts.domain.pojo.book_ticket_full

import com.bitla.ts.domain.pojo.ticket_details.response.DropOffDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("boarding_details")
    val boarding_details: BoardingDetails,
    @SerializedName("drop_off_details")
    val dropOffDetails: DropOffDetails? = null,
    val booked_by: String,
    val bus_type: String,
    val dep_time: String,
    val dest_id: Int,
    val destination: String,
    val duration: String,
    val issued_on: String,
    val mobile_terms_and_conditions: String,
    val no_of_seats: Int,
    val origin: String,
    val origin_id: Int,
    val passenger_details: List<PassengerDetail>,
    val res_id: Long,
    val seat_numbers: String,
    val service_number: String,
    val ticket_number: String,
    val ticket_status: String,
    val total_fare: Double,
    val travel_date: String,
    val payment_initiatives: String,
    @SerializedName("ezetap_user_name")
    @Expose var ezetapUserName: String = "",
    
    @SerializedName("ezetap_api_key")
    @Expose
    var ezetapApiKey: String = "",
    @Expose
    @SerializedName("agent_recharge_qr_resp")
    var agentRechargeQrResp: String? = null,
    @SerializedName("paytm_pos_details")
    @Expose
    val paytmPosDetails: PaytmPosDetails? = null,
    @SerializedName("payment_mode")
    @Expose
    val paymentMode: PaymentMode? = null,

    @SerializedName("branch_upi_qr_resp")
    var branchUpiQrResp: String? = null,
    @SerializedName("branch_sms_resp")
    var branchSmsResp: Boolean,
    @SerializedName("branch_vpa_resp")
    var branchVpaResp: Boolean,

    // PhonePe V2
    @SerializedName("is_phonepe_v2_payment")
    var isPhonePeV2Payment: Boolean,
    @SerializedName("orderId")
    var orderId: String,
    @SerializedName("merchant_id")
    var merchantId: String,
    @SerializedName("token")
    var token: String,
    @SerializedName("is_live_environment")
    var isLiveEnvironment: Boolean?,
    )