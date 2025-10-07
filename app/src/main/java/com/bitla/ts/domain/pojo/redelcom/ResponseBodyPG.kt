package com.bitla.ts.domain.pojo.redelcom

import com.bitla.ts.domain.pojo.book_ticket_full.Result
import com.google.gson.annotations.SerializedName

data class ResponseBodyPG(
    val status: Int,
    val code: Int,
    val result: Result,
    val message: String,
    )