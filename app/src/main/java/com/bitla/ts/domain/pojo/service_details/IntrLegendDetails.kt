package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class IntrLegendDetails {
    @SerializedName("Reserved_Branch")
    @Expose
    var reservedBranch: ReservedBranch? = null

    @SerializedName("Reserved_OA")
    @Expose
    var reservedOA: ReservedOA? = null

    @SerializedName("Reserved_Api")
    @Expose
    var reservedApi: ReservedApi? = null

    @SerializedName("Reserved_E_tic")
    @Expose
    var reservedETic: ReservedETic? = null

    @SerializedName("Reserved_L")
    @Expose
    var reservedL: ReservedL? = null

    @SerializedName("Quota")
    @Expose
    var quota: Quota? = null

    @SerializedName("E_Quota")
    @Expose
    var eQuota: EQuota? = null

    @SerializedName("Available")
    @Expose
    var available: Available? = null

    @SerializedName("Available_L")
    @Expose
    var availableL: AvailableL? = null

    @SerializedName("Available_G")
    @Expose
    var availableG: AvailableG? = null

    @SerializedName("Blocked")
    @Expose
    var blocked: Blocked? = null

    @SerializedName("Onhold")
    @Expose
    var onhold: Onhold? = null

    @SerializedName("Onhold_Ladies")
    @Expose
    var onholdLadies: OnholdLadies? = null

    @SerializedName("Blocked_L")
    @Expose
    var blockedL: BlockedL? = null

    @SerializedName("Offline_Agent_C")
    @Expose
    var offlineAgentC: OfflineAgentC? = null

    @SerializedName("VIP")
    @Expose
    var vIP: VIP? = null

    @SerializedName("Selected")
    @Expose
    var selected: Selected? = null

}