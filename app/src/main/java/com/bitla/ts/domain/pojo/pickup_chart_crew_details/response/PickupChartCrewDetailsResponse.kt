package com.bitla.ts.domain.pojo.pickup_chart_crew_details.response


import com.google.gson.annotations.SerializedName

data class PickupChartCrewDetailsResponse(
    @SerializedName("attendent")
    val attendent: String,
    @SerializedName("attendent_contact")
    val attendentContact: String,
    @SerializedName("attendent_id")
    val attendentId: String,
    @SerializedName("chart_operated_by")
    val chartOperatedBy: String,
    @SerializedName("checking_inspector")
    val checkingInspector: String,
    @SerializedName("checking_inspector_contact")
    val checkingInspectorContact: String,
    @SerializedName("checking_inspector_id")
    val checkingInspectorId: String,
    @SerializedName("cleaner")
    val cleaner: String,
    @SerializedName("cleaner_contact")
    val cleanerContact: String,
    @SerializedName("cleaner_id")
    val cleanerId: String,
    @SerializedName("Coach")
    val coach: String,
    @SerializedName("code")
    val code: Int,
    @SerializedName("driver1")
    val driver1: String,
    @SerializedName("driver_1_contact")
    val driver1Contact: String,
    @SerializedName("driver_1_id")
    val driver1Id: String,
    @SerializedName("driver2")
    val driver2: String,
    @SerializedName("driver3")
    val driver3: String,
    @SerializedName("driver_2_contact")
    val driver2Contact: String,
    @SerializedName("driver_2_id")
    val driver2Id: String,
   @SerializedName("driver_3_id")
    val driver3Id: String,
    @SerializedName("driver_3_contact")
    val driver3contact: String,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("last_location")
    val lastLocation: String
)