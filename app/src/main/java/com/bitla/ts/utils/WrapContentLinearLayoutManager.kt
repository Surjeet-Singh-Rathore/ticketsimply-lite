package com.bitla.ts.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class WrapContentLinearLayoutManager(linearLayoutManager: LinearLayoutManager, context: Context?) :
    LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.d("${e.message}")
        }
    }
}