package com.bitla.ts.domain.pojo.employees_details.response

import com.google.gson.annotations.SerializedName

data class EmployeesDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("employees")
    val employees: MutableList<Employee>,
    @SerializedName("show_contractor_as_attendent_in_bus_mobility_app")
    val showContractorAsAttendentInBusMobilityApp: String,
    @SerializedName("result")
    val result: Result?
)