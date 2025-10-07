package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCityListBinding

class CityListAdapterNew(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result>


) :
    RecyclerView.Adapter<CityListAdapterNew.ViewHolder>() {
    private var TAG: String = CityListAdapterNew::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCityListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchList[position]
        holder.tvCityName.text = item.name


        holder.layoutSearchSelection.setOnClickListener {
            onItemClickListener.onClickOfItem(item.name ?: "", item.id ?: 0)
        }
    }

    fun filterList(filteredNames: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result>) {
        this.searchList = filteredNames
        notifyDataSetChanged()
    }


    class ViewHolder(binding: ChildCityListBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCityName = binding.cityName
        val tickedCity = binding.selectedTick
        val layoutSearchSelection = binding.layoutnames
    }
}