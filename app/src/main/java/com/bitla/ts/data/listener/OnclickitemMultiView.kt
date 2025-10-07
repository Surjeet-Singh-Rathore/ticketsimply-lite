package com.bitla.ts.data.listener

import android.view.View

interface OnclickitemMultiView {
    fun onClickMuliView(
        view: View,
        view2: View,
        view3: View,
        view4: View,
        resID: String,
        remarks: String
    )

    fun onClickAdditionalData(
        view0 : View,
        view1 : View
    )



}