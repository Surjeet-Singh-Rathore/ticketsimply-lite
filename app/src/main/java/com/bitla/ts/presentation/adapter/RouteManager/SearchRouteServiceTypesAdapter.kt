package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterSearchRouteServiceTypesBinding
import com.bitla.ts.domain.pojo.route_manager.CitiesListData

class SearchRouteServiceTypesAdapter(
    private val context: Context,
    private var list: ArrayList<CitiesListData>,
    private val listener: DialogAnyClickListener,
) :
    RecyclerView.Adapter<SearchRouteServiceTypesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSearchRouteServiceTypesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        if (data.count != "") {
            holder.binding.titleTV.text = list[position].name + "(${data.count})"
        } else {
            holder.binding.titleTV.text = list[position].name
       }


        holder.binding.titleTV.setOnClickListener {
            listener.onAnyClickListener(1, "selctedOption", position)
        }
        if (list[position].isSelected) {
            holder.binding.titleTV.setBackgroundResource(R.drawable.bg_light_blue_round_stroke)
            holder.binding.titleTV.setTextColor(
                context.getResources().getColor(R.color.colorPrimary)
            )
            holder.binding.titleTV.setTypeface(null, Typeface.BOLD)

        } else {
            holder.binding.titleTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            holder.binding.titleTV.setTextColor(context.getResources().getColor(R.color.black))
            holder.binding.titleTV.setTypeface(null, Typeface.NORMAL)

        }

    }

    class ViewHolder(binding: AdapterSearchRouteServiceTypesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleTV = binding.titleTV
        val binding = binding


    }
}