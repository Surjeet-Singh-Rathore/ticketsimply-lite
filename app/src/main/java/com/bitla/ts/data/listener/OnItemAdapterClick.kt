package com.bitla.ts.data.listener

import android.view.View

interface OnItemAdapterClick {

    fun onItemClick(bool : Boolean, view: View, position: Int)

}