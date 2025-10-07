package com.bitla.ts.domain.pojo.update_ticket.request


import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("boarding_at")
    val boardingAt: BoardingAt? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("drop_off")
    val dropOff: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("fare")
    val fare: String? = null,

    @SerializedName("id_card")
    val idCard: String? = null,

    @SerializedName("id_card_no")
    val idCardNo: String? = null,

    @SerializedName("offline_agent")
    val offlineAgent: String? = null,

    @SerializedName("online_agent")
    val onlineAgent: String? = null,

    @SerializedName("remarks")
    val remarks: String? = null,

    @SerializedName("to_send_sms")
    val toSendSms: String? = null
)