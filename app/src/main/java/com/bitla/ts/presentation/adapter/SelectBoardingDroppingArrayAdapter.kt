package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.service_details_response.StageDetail

class SelectBoardingDroppingArrayAdapter(
    context: Context,
    @LayoutRes private var resource: Int,
    private val textViewResourceId: Int,
    private val objects: MutableList<StageDetail>,
    private val onClickListener: ItemClickListener
) : ArrayAdapter<StageDetail>(context, resource, objects) {
    private var mPois: MutableList<StageDetail> = objects

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): StageDetail {
        return mPois[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item, parent, false)
        view.findViewById<TextView>(textViewResourceId).apply {
            text = getItem(position).name.toString() + " " + getItem(position).time.toString()
        }
        view.findViewById<RelativeLayout>(R.id.itemLayout).apply {
            setOnClickListener {
                onClickListener.onItemSelected(position, getItem(position))
            }
        }
        return view
    }

    interface ItemClickListener {
        fun onItemSelected(position: Int, item: StageDetail)
    }
}