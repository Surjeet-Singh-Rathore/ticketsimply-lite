package com.bitla.ts.data.listener

import android.view.View

interface OnQuickBookListener {
    fun quickBook(
        view: View,
        isAdd: Boolean,
        position: Int,
        passengerCount: Int,
        totalPassengerCount: Int,
        label: String,
        id: Int,
        labelType: String,
        labelTypeId:Int,
        fare: Double
    )
}


