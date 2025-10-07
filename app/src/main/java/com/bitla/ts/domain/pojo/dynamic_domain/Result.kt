package com.bitla.ts.domain.pojo.dynamic_domain

import com.google.gson.annotations.SerializedName

data class  Result(
    @SerializedName("bcc_id")
    val bccId: Int = 0,
    @SerializedName("logo_url")
    val logoUrl: String = "",
    @SerializedName("dailing_code")
    val dailingCode: ArrayList<Int>?,
    @SerializedName("mba_url")
    val mbaUrl: String = "",
    @SerializedName("mobile_logo_url")
    val mobileLogoUrl: String = "",
    @SerializedName("is_encrypted")
    val isEncrypted: Boolean ? = false,
    @SerializedName("is_https_support")
    val isHttpsSupport: Boolean ? = false
)