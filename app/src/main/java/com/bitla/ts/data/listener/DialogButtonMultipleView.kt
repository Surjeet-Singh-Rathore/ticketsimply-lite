package com.bitla.ts.data.listener

import android.view.View

interface DialogButtonMultipleView {
    fun onLeftButtonClick(view: View?, view1: View?, view2: View?, view3: View?, resId: String)
    fun onRightButtonClick(
        view: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String,
        remark: String
    )
}