package com.bitla.ts.data.listener

interface DialogButtonUnblockSeatListener {
    fun onLeftButtonClick()
    fun onRightButtonClick(seats: String, selectionType: String, fromDate: String?, toDate: String?, remarks: String?)
}
