package com.example.buscoach.service_details_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ExtraSeatDetail: Serializable {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("age")
    @Expose
    var age: Any? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("origin_id")
    @Expose
    var originId: Int? = null

    @SerializedName("origin_name")
    @Expose
    var originName: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: Int? = null

    @SerializedName("destination_name")
    @Expose
    var destinationName: String? = null

    @SerializedName("from_to")
    @Expose
    var fromTo: String? = null

    @SerializedName("blocking_time")
    @Expose
    var blockingTime: Any? = null

    @SerializedName("seat_fare")
    @Expose
    var seatFare: String? = null

    @SerializedName("ticket_no")
    @Expose
    var ticketNo: String? = null

    @SerializedName("booking_fare")
    @Expose
    var bookingFare: String? = null

    @SerializedName("phone_num")
    @Expose
    var phoneNum: String? = null

    @SerializedName("boarding_stage")
    @Expose
    var boardingStage: String? = null

    @SerializedName("landmark")
    @Expose
    var landmark: String? = null

    @SerializedName("drop_off_stage")
    @Expose
    var dropOffStage: String? = null

    @SerializedName("seat_no")
    @Expose
    var seatNo: String? = null

    @SerializedName("booked_by")
    @Expose
    var bookedBy: String? = null

    @SerializedName("remarks")
    @Expose
    var remarks: String? = null

    @SerializedName("can_cancel")
    @Expose
    var canCancel: Boolean? = null

    @SerializedName("can_cancel_ticket_for_user")
    @Expose
    var canCancelTicketForUser: Boolean? = null

    @SerializedName("can_shift_ticket")
    @Expose
    var canShiftTicket: Boolean? = null

    @SerializedName("is_phone_block")
    @Expose
    var isPhoneBlock: Boolean? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: Int? = null

    @SerializedName("seat_numbers")
    @Expose
    var seatNumbers: String? = null

    @SerializedName("dep_time")
    @Expose
    var depTime: String? = null

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null

    @SerializedName("booked_date")
    @Expose
    var bookedDate: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("is_his_booking")
    @Expose
    var isHisBooking: Boolean? = null

    @SerializedName("booked_by_id")
    @Expose
    var bookedById: Int? = null

    @SerializedName("travel_branch_id")
    @Expose
    var travelBranchId: Int? = null

    @SerializedName("background_color")
    @Expose
    var backgroundColor: String? = null


    @SerializedName("is_update_ticket")
    val isUpdateTicket: Boolean? = null
}