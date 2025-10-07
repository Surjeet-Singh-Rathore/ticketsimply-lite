package com.example.buscoach.service_details_response


import com.google.gson.annotations.SerializedName
import java.io.Serializable


class IntrLegendDetails: Serializable {

    @SerializedName("Reserved_Branch")
    var reservedBranch: ReservedBranch? = null

    @SerializedName("Reserved_OA")
    var reservedOA: ReservedOA? = null

    @SerializedName("Reserved_Api")
    var reservedApi: ReservedApi? = null

    @SerializedName("Reserved_E_tic")
    var reservedETic: ReservedETic? = null

    @SerializedName("Reserved_L")
    var reservedL: ReservedL? = null

    @SerializedName("Quota")
    var quota: Quota? = null

    @SerializedName("E_Quota")
    var eQuota: EQuota? = null

    @SerializedName("Available")
    var available: Available? = null

    @SerializedName("Available_L")
    var availableL: AvailableL? = null

    @SerializedName("Blocked")
    var blocked: Blocked? = null

    @SerializedName("Onhold")
    var onhold: Onhold? = null

    @SerializedName("Onhold_Ladies")
    var onholdLadies: OnholdLadies? = null

    @SerializedName("Blocked_L")
    var blockedL: BlockedL? = null

    @SerializedName("VIP")
    var vip: Vip? = null

    @SerializedName("Social Distance Quota")
    var socialDistanceQuota: SocialDistanceQuota? = null

    @SerializedName("Selected")
    var selected: Selected? = null
}
