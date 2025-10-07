package com.bitla.ts.domain.pojo.instant_recharge

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PgData (
    @SerializedName("id")
    val id: String = "",
    @SerializedName("pg_name")
    val pgName: String = "",
    @SerializedName("is_selected")
    var isSelected: Boolean = false,
    @SerializedName("pg_name_paybitla" )
    var pgNamePayBitla : String = ""
)