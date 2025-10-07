package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

import com.google.gson.annotations.SerializedName

class PinBasedActionPrivileges (

    @SerializedName("ticket_confirmation")
    var ticketConfirmation: Boolean? = false,

    @SerializedName("exclude_ticket_confirmation")
    var excludeTicketConfirmation: MutableList<ExcludeTicketConfirmation>? = null,

    @SerializedName("ticket_shifting")
    var ticketShifting: Boolean? = false,

    @SerializedName("extend_fare_settings")
    var extendFareSetting: Boolean? = false,

    @SerializedName("single_page_block_unblock")
    var singlePageBlockUnblock: Boolean? = false,

    @SerializedName("ticket_cancellation")
    var ticketCancellation: Boolean? = false,

    @SerializedName("ticket_move_to_seat_extra_seat")
    var ticketMoveToSeatExtraSeat: Boolean? = false,

    @SerializedName("fare_change_for_multiple_services")
    var fareChangeForMultipleServices: Boolean? = false,

    @SerializedName("allow_block_services")
    var allowBlockServices: Boolean? = false,

    @SerializedName("phone_blocking")
    var phoneBlocking: Boolean? = false,

    @SerializedName("modify_reservation")
    var modifyReservation: Boolean? = false,

    @SerializedName("route_schedule_status_chahge")
    var routeStatusChange: Boolean? = false,

    @SerializedName("phone_blocking_release")
    var phoneBlockingRelease: Boolean? = false,

    @SerializedName("rate_card")
    var rateCard: Boolean? = false,

    @SerializedName("rote_update")
    var routeUpdate: Boolean? = false,

    @SerializedName("extra_seat_book")
    var extraSeatBook: Boolean? = false

)
