package com.bitla.ts.domain.pojo.pinelabs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailData {
    @SerializedName("TransactionType")
    @Expose
    var transactionType: Int? = null

    @SerializedName("RetrievalReferenceNumber")
    @Expose
    var retrievalReferenceNumber: String? = null

    @SerializedName("HostResponse")
    @Expose
    var hostResponse: String? = null

    @SerializedName("AcquirerName")
    @Expose
    var acquirerName: String? = null

    @SerializedName("BillingRefNo")
    @Expose
    var billingRefNo: String? = null

    @SerializedName("CardNumber")
    @Expose
    var cardNumber: String? = null

    @SerializedName("MerchantAddress")
    @Expose
    var merchantAddress: String? = null

    @SerializedName("InvoiceNumber")
    @Expose
    var invoiceNumber: Int? = null

    @SerializedName("TransactionDate")
    @Expose
    var transactionDate: String? = null

    @SerializedName("TerminalId")
    @Expose
    var terminalId: String? = null

    @SerializedName("TransactionTime")
    @Expose
    var transactionTime: String? = null

    @SerializedName("CardType")
    @Expose
    var cardType: String? = null

    @SerializedName("LoyaltyPointsAwarded")
    @Expose
    var loyaltyPointsAwarded: Int? = null

    @SerializedName("PosEntryMode")
    @Expose
    var posEntryMode: Int? = null

    @SerializedName("PlutusVersion")
    @Expose
    var plutusVersion: String? = null

    @SerializedName("ApprovalCode")
    @Expose
    var approvalCode: String? = null

    @SerializedName("CardEntryMode")
    @Expose
    var cardEntryMode: String? = null

    @SerializedName("AcquiringBankCode")
    @Expose
    var acquiringBankCode: String? = null

    @SerializedName("Remark")
    @Expose
    var remark: String? = null

    @SerializedName("PrintCardholderName")
    @Expose
    var printCardholderName: Int? = null

    @SerializedName("MerchantCity")
    @Expose
    var merchantCity: String? = null

    @SerializedName("AuthAmoutPaise")
    @Expose
    var authAmoutPaise: String? = null

    @SerializedName("MerchantName")
    @Expose
    var merchantName: String? = null

    @SerializedName("MerchantId")
    @Expose
    var merchantId: String? = null

    @SerializedName("CardholderName")
    @Expose
    var cardholderName: String? = null

    @SerializedName("ExpiryDate")
    @Expose
    var expiryDate: String? = null

    @SerializedName("BatchNumber")
    @Expose
    var batchNumber: Int? = null

    @SerializedName("PlutusTransactionLogID")
    @Expose
    var plutusTransactionLogID: String? = null
}