package com.bitla.ts.presentation.adapter

import android.annotation.*
import android.content.*
import android.view.*
import android.widget.*
import androidx.annotation.*
import com.bitla.ts.*
import com.bitla.ts.domain.pojo.all_coach.response.*

class AllCoachCustomArrayAdapter(
    context: Context,
    @LayoutRes private var resource: Int,
    private val textViewResourceId: Int,
    private var list: MutableList<AllCoach>,
    // private val onClickListener: ((origin:Origin)->Unit)
) : ArrayAdapter<AllCoach>(context, resource, list) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): AllCoach {
        return list[position]
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, parent, false)
        view.findViewById<TextView>(textViewResourceId).text = list[position].name
        return view
    }
    fun updateList(newList:MutableList<AllCoach>){
        list = newList
        notifyDataSetChanged()
    }
}