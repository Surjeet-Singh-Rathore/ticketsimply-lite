package com.bitla.ts.domain.pojo.self_audit_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Option {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null
}