package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.annotation.LayoutRes
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.SpinnerItems

class SelectUserTypeArrayAdapter(
    context: Context,
    @LayoutRes private var resource: Int,
    private val textViewResourceId: Int,
    private val objects: MutableList<SpinnerItems>,
    private val selectedUserTypeList: MutableList<SpinnerItems>,
    private val isAllowMultipleQuota: Boolean,
    private val onClickListener: ItemClickListener
) : ArrayAdapter<SpinnerItems>(context, resource, objects) {
    private var mPois: MutableList<SpinnerItems> = objects

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): SpinnerItems {
        return mPois[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item_witch_checkbox, parent, false)
        view.findViewById<CheckBox>(textViewResourceId).apply {
            if (selectedUserTypeList.contains(getItem(position)))
                isChecked = true
            text = getItem(position).value
            setOnCheckedChangeListener { compoundButton, b ->
                if (!isAllowMultipleQuota) {
                    selectedUserTypeList.clear()
                }
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
        fun onSelected(position: Int, item: SpinnerItems)
        fun onDeselect(position: Int, item: SpinnerItems)
    }

}