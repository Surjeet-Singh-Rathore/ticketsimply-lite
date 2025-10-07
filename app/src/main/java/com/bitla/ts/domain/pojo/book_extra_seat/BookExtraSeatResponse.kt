package com.bitla.ts.domain.pojo.book_extra_seat

import com.bitla.ts.domain.pojo.book_extra_seat.request.BoardingDetails
import com.bitla.ts.domain.pojo.book_ticket_full.PaytmPosDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookExtraSeatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    val boarding_details: BoardingDetails,
    val bus_type: String,
    val dep_time: String,
    val destination: String,
    val duration: String,
    val no_of_seats: Int,
    val origin: String,
    val passenger_details: List<PassengerDetail>,
    val seat_numbers: String,
    val service_number: String,
    val total_fare: String,
    val total_fare_extra: Double,
    val travel_date: String,
    @SerializedName("payment_initiatives")
    val paymentInitiatives: String,
    @SerializedName("terminal_id")
    val terminalId: Any,
    @SerializedName("terminal_ref_no")
    val terminalRefNo: String,
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("ezetap_user_name")
    @Expose
    var ezetapUserName: String = "",
    
    @SerializedName("ezetap_api_key")
    @Expose
    var ezetapApiKey: String = "",
    
    var ticket_status: String = "",
    @SerializedName("agent_recharge_qr_resp")
    var agentRechargeQrResp: String? = null,
    
    @SerializedName("paytm_pos_details")
    @Expose
    val paytmPosDetails: PaytmPosDetails? = null,

    @SerializedName("branch_upi_qr_resp")
    var branchUpiQrResp: String? = null,
    @SerializedName("branch_upi_sms_resp")
    var branchUpiSmsResp: Boolean,
    @SerializedName("branch_upi_vpa_resp")
    var branchUpiVpaResp: Boolean,

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