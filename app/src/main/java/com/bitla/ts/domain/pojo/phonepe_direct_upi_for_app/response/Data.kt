package com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("code")
    val code: String?,
    @SerializedName("data")
    val `data`: DataX?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("qr_string")
    val qrString: String?,
    @SerializedName("png_url")
    val pngUrl: String?,

)