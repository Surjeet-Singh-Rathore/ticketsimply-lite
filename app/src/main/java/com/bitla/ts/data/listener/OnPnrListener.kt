package com.bitla.ts.data.listener

interface OnPnrListener {
    fun onPnrSelection(tag: String, pnr: Any, doj: Any? = null)
}


