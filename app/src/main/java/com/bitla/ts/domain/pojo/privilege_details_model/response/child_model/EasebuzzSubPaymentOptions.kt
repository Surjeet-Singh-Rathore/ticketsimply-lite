package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

import com.google.gson.annotations.SerializedName

data class EasebuzzSubPaymentOptions(
    @SerializedName("PAY_VIA_QR")
    val payViaQr: Int? = null,

    @SerializedName("PAY_VIA_SMS")
    val payViaSms: Int? = null,

    @SerializedName("PAY_VIA_UPI")
    val payViaUpi: Int? = null,

    @SerializedName("PAY_VIA_UPI_INTENT")
val payViaUpiIntent: Int? = null


)