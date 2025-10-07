package com.bitla.ts.data.listener


interface OnItemPinnedListener {
    fun onItemClick(isPinned: String, position: Int, label: String)
}