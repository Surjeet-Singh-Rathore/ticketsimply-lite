package com.bitla.ts.domain.pojo.book_ticket_full

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PaytmPosDetails {


    @SerializedName("payment_type")
    @Expose
    val paymentType: String = ""

    @SerializedName("user_name")
    @Expose
    val userName: String = ""
    @SerializedName("merchant")
    @Expose
    val merchant:String=""
    @SerializedName("working_key")
    @Expose
    val workingKey:String=""
    @SerializedName("payment_url")
    @Expose
    val  paymentUrl:String=""
    @SerializedName("amount")
    @Expose
    val  amount:String=""
    @SerializedName("device_id")
    @Expose
    val  deviceId:String=""
    @SerializedName("channel_id")
    @Expose
    val channelId:String=""

    @SerializedName("checksum")
    @Expose
    val checksum:String=""

    @SerializedName("version")
    @Expose
    val version:String=""

    @SerializedName("merchantReferenceNo")
    val merchantReferenceNo: String=""
    @SerializedName("merchantTransactionId")
    val merchantTransactionId: String=""
    @SerializedName("paytmMid")
    val paytmMid: String=""
    @SerializedName("paytmTid")
    val paytmTid: String=""
    @SerializedName("transactionAmount")
    val transactionAmount: String=""
    @SerializedName("transactionDateTime")
    val transactionDateTime: String=""


}