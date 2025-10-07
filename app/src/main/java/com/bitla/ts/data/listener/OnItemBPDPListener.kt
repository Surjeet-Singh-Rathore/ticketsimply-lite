package com.bitla.ts.data.listener

import android.view.View

interface OnItemBPDPListener {
    fun onClickBPDPData(view: View, data: String, id: Int, position: Int)
}