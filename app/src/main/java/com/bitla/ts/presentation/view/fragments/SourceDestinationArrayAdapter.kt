package com.bitla.ts.presentation.view.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bitla.ts.domain.pojo.destination_pair.Origin
import com.bitla.ts.R

class SourceDestinationArrayAdapter(
    context: Context,
    @LayoutRes private var resource: Int,
    private val textViewResourceId: Int,
    private var list: MutableList<Origin>,
   // private val onClickListener: ((origin:Origin)->Unit)
) : ArrayAdapter<Origin>(context, resource, list) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Origin? {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item, parent, false)
        view.findViewById<TextView>(textViewResourceId).text = list[position].name
        return view
    }
    fun updateList(newList:MutableList<Origin>){
        list = newList
        notifyDataSetChanged()
    }
}