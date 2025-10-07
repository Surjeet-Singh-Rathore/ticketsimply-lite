package com.bitla.ts.data.listener

interface DialogButtonAnyDataListener {
    fun onDataSend(type: Int,file : Any)

    fun onDataSendWithExtraParam(type: Int,file: Any,extra : Any)


}
