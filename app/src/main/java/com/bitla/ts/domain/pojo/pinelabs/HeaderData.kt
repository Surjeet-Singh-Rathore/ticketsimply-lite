package com.bitla.ts.domain.pojo.pinelabs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HeaderData {
    @SerializedName("MethodId")
    @Expose
    var methodId: String? = null

    @SerializedName("VersionNo")
    @Expose
    var versionNo: String? = null

    @SerializedName("ApplicationId")
    @Expose
    var applicationId: String? = null

    @SerializedName("UserId")
    @Expose
    var userId: String? = null
}