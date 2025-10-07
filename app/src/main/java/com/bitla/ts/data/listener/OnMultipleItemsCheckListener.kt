package com.bitla.ts.data.listener

import com.bitla.ts.domain.pojo.SpinnerItems


interface OnMultipleItemsCheckListener {
    fun onItemCheck(item: SpinnerItems?)
    fun onItemUncheck(item: SpinnerItems?)
}