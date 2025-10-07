package com.bitla.ts.domain.pojo.book_ticket_full

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class PaymentMode {
    @SerializedName("pg_name")
    @Expose
    var pgName: String? = null

    @SerializedName("access_key")
    @Expose
    var accessKey: String? = null

    @SerializedName("upi_link")
    @Expose
    var upiLink: String? = null
}