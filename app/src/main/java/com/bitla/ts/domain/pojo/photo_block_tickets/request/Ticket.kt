package com.bitla.ts.domain.pojo.photo_block_tickets.request


import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("transaction_number")
    val transactionNumber: String
)