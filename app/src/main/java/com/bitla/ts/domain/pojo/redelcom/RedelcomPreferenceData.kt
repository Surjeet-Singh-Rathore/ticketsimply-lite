package com.bitla.ts.domain.pojo.redelcom

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RedelcomPreferenceData(
    var terminalId: String = "",
    var api_key: String = "",
    var client_id: String = "",
    var redelcom_uri: String = "",
    var is_redelcom_enabled: Boolean = false,
    ):Parcelable