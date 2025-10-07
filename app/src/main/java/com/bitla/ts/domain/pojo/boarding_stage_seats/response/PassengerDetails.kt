package com.bitla.ts.domain.pojo.boarding_stage_seats.response


import com.google.gson.annotations.SerializedName

data class PassengerDetails(
    @SerializedName("age")
    val age: Int?,
    @SerializedName("blocking_time")
    val blockingTime: Any?,
    @SerializedName("boarding_stage")
    val boardingStage: String?,
    @SerializedName("booked_by")
    val bookedBy: String?,
    @SerializedName("booked_by_id")
    val bookedById: Int?,
    @SerializedName("booked_date")
    val bookedDate: String?,
    @SerializedName("booking_fare")
    val bookingFare: String?,
    @SerializedName("can_cancel")
    val canCancel: Boolean?,
    @SerializedName("can_cancel_ticket_for_user")
    val canCancelTicketForUser: Boolean?,
    @SerializedName("can_shift_ticket")
    val canShiftTicket: Boolean?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("dep_time")
    val depTime: String?,
    @SerializedName("destination_id")
    val destinationId: Int?,
    @SerializedName("destination_name")
    val destinationName: String?,
    @SerializedName("drop_off_stage")
    val dropOffStage: String?,
    @SerializedName("from_to")
    val fromTo: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("is_eticket")
    val isEticket: Boolean?,
    @SerializedName("is_his_booking")
    val isHisBooking: Boolean?,
    @SerializedName("is_phone_block")
    val isPhoneBlock: Boolean?,
    @SerializedName("is_temporary_phone_block")
    val isTemporaryPhoneBlock: Boolean?,
    @SerializedName("is_update_ticket")
    val isUpdateTicket: Boolean?,
    @SerializedName("landmark")
    val landmark: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("no_of_seats")
    val noOfSeats: Int?,
    @SerializedName("origin_id")
    val originId: Int?,
    @SerializedName("origin_name")
    val originName: String?,
    @SerializedName("phone_num")
    val phoneNum: String?,
    @SerializedName("remarks")
    val remarks: String?,
    @SerializedName("seat_fare")
    val seatFare: String?,
    @SerializedName("seat_no")
    val seatNo: String?,
    @SerializedName("seat_numbers")
    val seatNumbers: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("ticket_no")
    val ticketNo: String?,
    @SerializedName("travel_branch_id")
    val travelBranchId: Int?,
    @SerializedName("travel_date")
    val travelDate: String?
)