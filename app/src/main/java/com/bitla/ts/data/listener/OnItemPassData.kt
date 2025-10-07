package com.bitla.ts.data.listener

import android.view.View

interface OnItemPassData {
    fun onItemData(view: View, str1: String, str2: String)

    //    fun onItemDataMore(view: View, str1: String, str2: String, str3: String, str4:String, str5:String)
    fun onItemDataMore(view: View, str1: String, str2: String, str3: String)
//    fun onItemArrayData(view: View, position: Int, data: Service)

}