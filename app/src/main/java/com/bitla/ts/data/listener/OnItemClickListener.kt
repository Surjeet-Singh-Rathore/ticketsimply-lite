package com.bitla.ts.data.listener

import android.app.Dialog
import android.view.View
import com.bitla.ts.domain.pojo.available_routes.Result

interface OnItemClickListener {
    fun onClickOfNavMenu(position: Int)
    fun onClick(view: View, position: Int)
    fun onButtonClick(view: Any,dialog : Dialog)
    fun onClickOfItem(data: String,position: Int)
    fun onMenuItemClick(itemPosition : Int,menuPosition:Int,busData : Result)
}


