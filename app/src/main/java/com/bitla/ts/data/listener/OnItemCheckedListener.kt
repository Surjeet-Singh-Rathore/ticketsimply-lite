package com.bitla.ts.data.listener

import android.view.View

interface OnItemCheckedListener {
    fun onItemChecked(isChecked: Boolean, view: View, position: Int)
}
