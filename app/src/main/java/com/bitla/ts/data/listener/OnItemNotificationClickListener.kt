package com.bitla.ts.data.listener

import android.view.View

interface OnItemNotificationClickListener {
    fun onClick(view: View?, position: Int?, view2: View?, isMarkRead: Boolean?)
}