package com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse

import com.google.gson.annotations.*

data class Service(
    var expand: Boolean = false,
    @SerializedName("available_seats")
    var availableSeats: Int? = null,
    @SerializedName("is_edit_mode")
    var isEditMode: Boolean? = false,
    @SerializedName("bus_type")
    var busType: String? = null,
    @SerializedName("checking_inspector")
    var checkingInspector: String? = null,
    @SerializedName("checking_inspector_number")
    var checkingInspectorNumber: String? = null,
    @SerializedName("coach_number")
    var coachNumber: String? = null,
    @SerializedName("departure_time")
    var departureTime: String? = null,
    @SerializedName("destination")
    var destination: String? = null,
    @SerializedName("destination_id")
    var destinationId: Int? = null,
    @SerializedName("driver2_contact_number")
    var driver2ContactNumber: String? = null,
    @SerializedName("driver2_name")
    var driver2Name: String? = null,
    @SerializedName("driver3_contact_number")
    var driver3ContactNumber: String? = null,
    @SerializedName("driver3_name")
    var driver3Name: String? = null,
    @SerializedName("driver_contact_number")
    var driverContactNumber: String? = null,
    @SerializedName("driver_name")
    var driverName: String? = null,
    @SerializedName("duration")
    var duration: String? = null,
    @SerializedName("helper_contact_number")
    var helperContactNumber: String? = null,
    @SerializedName("helper_name")
    var helperName: String? = null,
    @SerializedName("last_boarding_stage_time")
    var lastBoardingStageTime: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("number")
    var number: String = "",
    @SerializedName("origin")
    var origin: String? = null,
    @SerializedName("origin_id")
    var originId: Int? = null,
    @SerializedName("phone_blocked_seat_count")
    var phoneBlockedSeatCount: Int? = null,
    @SerializedName("remarks")
    var remarks: String? = null,
    @SerializedName("reservation_id")
    var reservationId: Long? = null,
    @SerializedName("route_id")
    var routeId: String? = null,
    @SerializedName("seat_occupancy_percentage")
    var seatOccupancyPercentage: String = "",
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("total_amount")
    var totalAmount: Double? = null,
    @SerializedName("total_seats")
    var totalSeats: Int? = null,
    @SerializedName("travel_date")
    var travelDate: String? = null,
    @SerializedName("contractor_Name")
    var contractorName: String? = null,
    @SerializedName("contractor_contact")
    var contractorNumber: String? = null,
    @SerializedName("arrival_time")
    var arrivalTime: String? = null,
    @SerializedName("arrival_date")
    var arrivaldate: String? = null,
    @SerializedName("is_locked")
    var isLocked: Boolean? = null,
    @SerializedName("fare_str")
    var fareStr: String? = "",
    @SerializedName("seat_type_availability")
    var seat_type_availability: String? = "",
    @SerializedName("last_updated_by")
    var last_updated_by: String? = "",

    @SerializedName("all_service")
    var allService: String? = "",
    @SerializedName("inspection_status")
    var inspectionStatus: Boolean? = false,

    var isChecked: Boolean = true,

    @SerializedName("via_route")
    var viaRoute: Boolean? = false,

    @SerializedName("is_self_audit_form")
    var isSelfAuditForm: Boolean? = false
)