package com.bitla.ts.domain.pojo.fare_breakup.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChargeDetails(
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("charge")
    var charge: String? = "0.0"
) : Parcelable
