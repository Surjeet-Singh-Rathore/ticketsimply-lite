package com.bitla.ts.data.listener


interface DialogButtonMoveSeatExtraListener {
    fun onLeftButtonClick(string: String?)
    fun onRightButtonClick(remarks: String, seatNo: String, extraSeatNo: String, sms: Boolean)
}
