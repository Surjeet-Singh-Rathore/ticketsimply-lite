package com.bitla.ts.domain.pojo.photo_block_tickets.response


import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail
import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("boarding_details")
    val boardingDetails: BoardingDetails,
    @SerializedName("booked_by")
    val bookedBy: String,
   /* @SerializedName("bus_type")
    val busType: BusType,*/
    @SerializedName("dep_time")
    val depTime: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("issued_on")
    val issuedOn: String,
    @SerializedName("no_of_seats")
    val noOfSeats: Int,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("passenger_details")
    val passengerDetails: List<PassengerDetail>,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("service_number")
    val serviceNumber: String,
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("total_fare")
    val totalFare: String,
    @SerializedName("travel_date")
    val travelDate: String ,
    @SerializedName("status")
    val status: String,
    @SerializedName("agent_recharge_qr_resp")
    var agentRechargeQrResp: String = "",
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