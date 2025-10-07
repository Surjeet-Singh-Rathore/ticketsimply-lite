package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ChildRoundOutlineBinding
import com.bitla.ts.domain.pojo.destination_list.City
import com.bitla.ts.utils.constants.FREQUENT_SEARCH

class FrequentAgentSearchAdapter(
    private val context: Context,
    private val onItemPassData: OnItemPassData,
    private var cityList: MutableList<City>,
    private var lastSelectedCity: Int
) :
    RecyclerView.Adapter<FrequentAgentSearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildRoundOutlineBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city: City = cityList[position]
        holder.titleText.text = city.name

        if (lastSelectedCity == position){
            holder.titleText.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.colorPrimary
                )
            )
            holder.titleText.setTextColor(
                context.resources.getColor(
                    R.color.white
                )
            )
        }else
        {
            holder.titleText.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.white
                )
            )
            holder.titleText.setTextColor(
                context.resources.getColor(
                    R.color.colorPrimary
                )
            )
        }


        holder.mainLayout.setOnClickListener {
            lastSelectedCity = position
            notifyDataSetChanged()
            holder.mainLayout.tag = FREQUENT_SEARCH
            onItemPassData.onItemData(
                holder.mainLayout,
                city.name,
                city.id
            )
        }
    }

    class ViewHolder(binding: ChildRoundOutlineBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleText = binding.cityText
        val mainLayout = binding.mainLayout

        init {

        }
    }
}