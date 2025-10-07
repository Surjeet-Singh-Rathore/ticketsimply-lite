package com.bitla.ts.data.listener

import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail


interface OnItemCheckListener {
    fun onItemCheck(item: PassengerDetail?)
    fun onItemUncheck(item: PassengerDetail?)
}