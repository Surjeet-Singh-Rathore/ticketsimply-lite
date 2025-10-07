package com.bitla.ts.domain.pojo.ticket_details_phase_3

import androidx.annotation.DrawableRes

data class TicketDetailsSideBarOptionsModel(
    @DrawableRes val iconId: Int,
    val name: String,
    val onClick : (() -> Unit)
)