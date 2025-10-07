package com.bitla.ts.domain.pojo.redelcom

import com.google.gson.annotations.SerializedName

data class ReqBodyPrint(
    val printText: String,
    val terminalId: String,
    )