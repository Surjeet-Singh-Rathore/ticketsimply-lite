package com.example.buscoach.service_details_response


import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PassengerDetails: Serializable {

    @SerializedName("name")
    var name: String? = null

    @SerializedName("age")
    var age: Int? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("origin_id")
    var originId: Int? = null

    @SerializedName("origin_name")
    var originName: String? = null

    @SerializedName("destination_id")
    var destinationId: Int? = null

    @SerializedName("destination_name")
    var destinationName: String? = null

    @SerializedName("from_to")
    var fromTo: String? = null

    @SerializedName("blocking_time")
    var blockingTime: Any? = null

    @SerializedName("seat_fare")
    var seatFare: String? = null

    @SerializedName("ticket_no")
    var ticketNo: String? = null

    @SerializedName("booking_fare")
    var bookingFare: String? = null

    @SerializedName("phone_num")
    var phoneNum: String? = null

    @SerializedName("boarding_stage")
    var boardingStage: String? = null

    @SerializedName("landmark")
    var landmark: String? = null

    @SerializedName("seat_no")
    var seatNo: String? = null

    @SerializedName("online_agent")
    var onlineAgent: String? = null

    @SerializedName("booked_by")
    var bookedBy: String? = null

    @SerializedName("remarks")
    var remarks: String? = null

    @SerializedName("can_cancel")
    var canCancel: Boolean? = null

    @SerializedName("can_cancel_ticket_for_user")
    var canCancelTicketForUser: Boolean? = null

    @SerializedName("can_shift_ticket")
    var canShiftTicket: Boolean? = null

    @SerializedName("is_phone_block")
    var isPhoneBlock: Boolean? = null

    @SerializedName("no_of_seats")
    var noOfSeats: Int? = null

    @SerializedName("seat_numbers")
    var seatNumbers: String? = null

    @SerializedName("dep_time")
    var depTime: String? = null

    @SerializedName("travel_date")
    var travelDate: String? = null

    @SerializedName("booked_date")
    var bookedDate: String? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("is_his_booking")
    var isHisBooking: Boolean? = false

    @SerializedName("online_agent_id")
    var onlineAgentId: Int? = null

    @SerializedName("onbehalf_travel_branch_id")
    var onbehalfTravelBranchId: Int? = null

    @SerializedName("is_update_ticket")
    val isUpdateTicket: Boolean? = null

    @SerializedName("drop_off_stage")
    val dropOffStage: String? = null

    @SerializedName("can_release_phone_block")
    var canReleasePhoneBlock: Boolean = false

    @SerializedName("can_confirm_phone_block")
    var canConfirmPhoneBlock: Boolean = false

    @SerializedName("policy_number")
    var policy_number: String?= null

//    val isFromMiddleTier:Boolean=true,

    var isSelected = false


}
