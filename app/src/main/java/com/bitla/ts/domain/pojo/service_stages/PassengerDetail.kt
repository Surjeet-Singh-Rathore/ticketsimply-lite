package com.bitla.ts.domain.pojo.service_stages


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("agent_ref_number")
    val agentRefNumber: String?,
    @SerializedName("basic_amount")
    val basicAmount: Double?,
    @SerializedName("boarding_address")
    val boardingAddress: String?,
    @SerializedName("boarding_at")
    val boardingAt: String?,
    @SerializedName("boarding_at_id")
    val boardingAtId: Int?,
    @SerializedName("boarding_landmark")
    val boardingLandmark: String?,
    @SerializedName("booking_type_id")
    val bookingTypeId: Int?,
    @SerializedName("bording_date_time")
    val boardingDateTime: String?,
    @SerializedName("bp_stage_latitude")
    val bpStageLatitude: String?,
    @SerializedName("bp_stage_longitude")
    val bpStageLongitude: String?,
    @SerializedName("commission_amount")
    val commissionAmount: Double?,
    @SerializedName("dp_stage_latitude")
    val dpStageLatitude: String?,
    @SerializedName("dp_stage_longitude")
    val dpStageLongitude: String?,
    @SerializedName("drop_off")
    val dropOff: String?,
    @SerializedName("dropoff_id")
    val dropOffId: Int?,
    @SerializedName("id_card")
    val idCard: IdCard?,
    @SerializedName("is_primary")
    val isPrimary: Int?,
    @SerializedName("is_shifted")
    val isShifted: Boolean?,
    @SerializedName("net_amount")
    val netAmount: Double?,
    @SerializedName("pnr_number")
    val pnrNumber: String?,
    @SerializedName("service_tax_amount")
    val serviceTaxAmount: Double?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("temperature")
    val temperature: String?,
    @SerializedName("transaction_charges")
    val transactionCharges: Double?
)