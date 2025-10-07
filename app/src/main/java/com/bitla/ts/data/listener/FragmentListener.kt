package com.bitla.ts.data.listener

interface FragmentListener {
    fun sendData(position: Int) {
    }

    fun selectedPoint(str: String, tag: String, locationId: String? = null)

    fun sendPickupDropOffDetails(str: String, value: String, tag: String)
}