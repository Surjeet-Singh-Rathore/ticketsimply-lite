package com.bitla.ts.data.listener

import android.view.View

interface OnItemCheckedMultipledataListner {
    fun onItemChecked(
        isChecked: Boolean,
        view: View,
        data1: String,
        data2: String,
        data3: String,
        position: Int
    )


}