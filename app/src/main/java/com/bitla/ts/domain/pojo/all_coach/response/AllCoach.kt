package com.bitla.ts.domain.pojo.all_coach.response


import com.bitla.ts.domain.pojo.employees_details.response.Employee
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AllCoach(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("employees")
    var employees: MutableList<Employee?>?
) : Serializable