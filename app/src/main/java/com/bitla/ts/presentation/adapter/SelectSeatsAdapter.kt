package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.annotation.LayoutRes
import com.bitla.ts.R


class SelectSeatsAdapter(
    context: Context,
    @LayoutRes private var resource: Int,
    private val textViewResourceId: Int,
    private val objects: MutableList<String>,
    private val selectedSeatsList: MutableList<String>,
    private val onClickListener: ItemClickListener
) : ArrayAdapter<String>(context, resource, objects) {
    private var mPois: MutableList<String> = objects

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): String {
        return mPois[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item_witch_checkbox, parent, false)
        view.findViewById<CheckBox>(textViewResourceId).apply {
            if (selectedSeatsList.contains(getItem(position)))
                isChecked = true
            text = getItem(position)
            setOnCheckedChangeListener { compoundButton, b ->

                if (b) {
                    onClickListener.onSelected(position, getItem(position))
                } else {
                    onClickListener.onDeselect(position, getItem(position))
                }

            }
        }

        return view
    }

    interface ItemClickListener {
        fun onSelected(position: Int, item: String)
        fun onDeselect(position: Int, item: String)
    }

}