package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.R

class SliderAdapter(
    val viewPager: ViewPager2,
    private var list: ArrayList<Int>,
    private val names: ArrayList<String>
) : RecyclerView.Adapter<SliderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.child_slider_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.view.setBackgroundResource(list[position])
        holder.imageTxt.text = names[position]
        if (position == list.size - 2) {
            viewPager.post(run)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val run = Runnable {
        list.addAll(list)
        names.addAll(names)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: AppCompatImageView = itemView.findViewById(R.id.view)
        var imageTxt: TextView = itemView.findViewById(R.id.imageTxt) as TextView
    }
}