package com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("agent_id")
    val agentId: Int,
    @SerializedName("agent_name")
    val agentName: String?,
    @SerializedName("branch_name")
    val branchName: String?,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_on")
    val createdOn: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("dd_cheque_no")
    val ddChequeNo: String,
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_no")
    val transactionNo: String,
    @SerializedName("transaction_type")
    val transactionType: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("updated_on")
    val updatedOn: String
)