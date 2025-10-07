package com.bitla.ts.data.listener

import com.bitla.ts.domain.pojo.destination_pair.SearchModel


interface OnItemCheckMultipleItemListener {
    fun onItemCheck(item: SearchModel)
    fun onItemUncheck(item: SearchModel)
}