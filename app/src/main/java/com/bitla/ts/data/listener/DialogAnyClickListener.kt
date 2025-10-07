package com.bitla.ts.data.listener

import android.view.View

interface DialogAnyClickListener {
    fun onAnyClickListener(type: Int,view: Any, position: Int)
    fun onAnyClickListenerWithExtraParam(type: Int,view: Any, list: Any,position: Int,outPos : Int)


}
