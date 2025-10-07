package com.bitla.ts.domain.pojo.expenses_details.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("crew_expenses")
    val crewExpenses: List<CrewExpense>,
    @SerializedName("vehicle_expenses")
    val vehicleExpenses: List<VehicleExpense>,
    @SerializedName("general_details")
    val generalDetails: List<GeneralDetails>
)