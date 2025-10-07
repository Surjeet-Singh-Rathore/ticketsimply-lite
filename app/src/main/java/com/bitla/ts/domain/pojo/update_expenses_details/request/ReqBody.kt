package com.bitla.ts.domain.pojo.update_expenses_details.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("crew_expenses")
    val crewExpenses: List<CrewExpense>,
    @SerializedName("reservation_id")
    val reservationId: String,
    @SerializedName("vehicle_expenses")
    val vehicleExpenses: List<VehicleExpense>,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("general_details")
    val generalDetails: List<GeneralDetailsReqBody> = mutableListOf()
)