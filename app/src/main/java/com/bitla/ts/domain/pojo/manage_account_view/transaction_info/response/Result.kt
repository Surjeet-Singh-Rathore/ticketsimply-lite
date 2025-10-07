package com.bitla.ts.domain.pojo.manage_account_view.transaction_info.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("agent_id")
    val agentId: Int,
    @SerializedName("agent_name")
    val agentName: String?,
    @SerializedName("branch_name")
    val branchName: String?,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_on")
    val createdOn: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("dd_cheque_no")
    val ddChequeNo: Any,
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_no")
    val transactionNo: String,
    @SerializedName("transaction_type")
    val transactionType: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("updated_on")
    val updatedOn: String
)