package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LegendDetails {
    @SerializedName("Reserved(Branch)")
    @Expose
    var reservedBranch: String? = null

    @SerializedName("Reserved(OA)")
    @Expose
    var reservedOA: String? = null

    @SerializedName("Reserved(Api)")
    @Expose
    var reservedApi: String? = null

    @SerializedName("Reserved(E-tic)")
    @Expose
    var reservedETic: String? = null

    @SerializedName("Reserved(L)")
    @Expose
    var reservedL: String? = null

    @SerializedName("Quota")
    @Expose
    var quota: String? = null

    @SerializedName("E-Quota")
    @Expose
    var eQuota: String? = null

    @SerializedName("Available")
    @Expose
    var available: String? = null

    @SerializedName("Available(L) ")
    @Expose
    var availableL: String? = null

    @SerializedName("Available(G)")
    @Expose
    var availableG: String? = null

    @SerializedName("Blocked")
    @Expose
    var blocked: String? = null

    @SerializedName("Onhold")
    @Expose
    var onhold: String? = null

    @SerializedName("Onhold Ladies")
    @Expose
    var onholdLadies: String? = null

    @SerializedName("Blocked(L)")
    @Expose
    var blockedL: String? = null

    @SerializedName("Offline Agent(C)")
    @Expose
    var offlineAgentC: String? = null

    @SerializedName("VIP")
    @Expose
    var vIP: String? = null

    @SerializedName("Selected")
    @Expose
    var selected: String? = null

}