package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.InterCityAdapterChildBinding
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.destination_list.City
import com.bitla.ts.utils.constants.DROPPING_SELECTION
import gone
import visible

class InterCityAgentAdapter(
    private val context: Context,
    private val onItemPassData: OnItemPassData,
    private var cityList: MutableList<City>,
    private val isFromMergeBus: Boolean,
) :
    RecyclerView.Adapter<InterCityAgentAdapter.ViewHolder>(), OnItemPassData {
    private val viewPool = RecyclerView.RecycledViewPool()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            InterCityAdapterChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city: City = cityList[position]
        holder.tvSearch.text = city.name
        holder.boardingDroppingHeading.text = context.getString(R.string.dropping_points).uppercase()
        holder.tvCityLabel.text = context.getString(R.string.city).uppercase()

        if(isFromMergeBus){
            holder.rvStation.gone()
            holder.boardingDroppingHeading.gone()
            holder.tvCityLabel.gone()
        }else{
            holder.rvStation.visible()
            holder.boardingDroppingHeading.visible()
            holder.tvCityLabel.visible()
            val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            val childSortSublistAdapter = InterStationAgentAdapter(
                context,
                this,
                cityList[position].dropping_point as MutableList<DropOffDetail>
            )
            layoutManager.initialPrefetchItemCount = cityList.size
            holder.rvStation.layoutManager = layoutManager
            holder.rvStation.adapter = childSortSublistAdapter
            holder.rvStation.setRecycledViewPool(viewPool)
        }
        holder.tvSearch.setOnClickListener {
            holder.tvSearch.tag= DROPPING_SELECTION
            onItemData(holder.tvSearch,city.name,city.id)
        }

    }

    fun filterList(filteredCities: MutableList<City>) {
        this.cityList = filteredCities
        notifyDataSetChanged()
    }


    class ViewHolder(binding: InterCityAdapterChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSearch = binding.tvSearch
        val layoutSearchSelection = binding.layoutSearchSelection
        val rvStation = binding.cityStation
        val boardingDroppingHeading = binding.boardingDroppingHeading
        val tvCityLabel = binding.tvCityLabel
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        onItemPassData.onItemData(view, str1, str2)
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {

    }
}