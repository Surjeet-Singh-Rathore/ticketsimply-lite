package com.bitla.ts.domain.pojo.employees_details.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Employee(
    @SerializedName("employee_type")
    val employeeType: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobile_number")
    val mobileNumber: String
) : Serializable