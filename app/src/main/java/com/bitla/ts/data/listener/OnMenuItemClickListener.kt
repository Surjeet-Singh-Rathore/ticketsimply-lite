package com.bitla.ts.data.listener

interface OnMenuItemClickListener {

    fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        label: String
    )
}


