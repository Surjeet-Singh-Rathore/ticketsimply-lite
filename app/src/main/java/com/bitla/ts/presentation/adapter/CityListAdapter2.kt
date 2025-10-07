package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.city_details.response.Result

class CityListAdapter2(
    context: Context,
    private val cityList: MutableList<Result>,
    private val onItemSelected: ((itemId: Int?, position: Int, itemName: String) -> Unit)
) : ArrayAdapter<Result>(context, R.layout.spinner_dropdown_item, cityList) {

    var list = cityList

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Result {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item, parent, false)
        view.findViewById<TextView>(R.id.tvItem).apply {
            text = list[position].name


        }

        view.findViewById<RelativeLayout>(R.id.itemLayout).apply {

            setOnClickListener {
                onItemSelected.invoke(
                    list[position].id ?: -1,
                    position,
                    list[position].name ?: "All Cities"
                )
            }
        }


        return view
    }

    fun updateList(newList: MutableList<Result>) {
        list = newList
        notifyDataSetChanged()
    }
}