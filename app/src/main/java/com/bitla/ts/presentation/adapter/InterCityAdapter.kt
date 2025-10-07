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
import com.bitla.ts.domain.pojo.destination_pair.SearchModel

class InterCityAdapter(
    private val context: Context,
    private val onItemPassData: OnItemPassData,
    private var searchList: MutableList<SearchModel>,
    private var fromOrigin: Boolean,
    private var stationList: ArrayList<SearchModel>,
) :
    RecyclerView.Adapter<InterCityAdapter.ViewHolder>(), OnItemPassData {
    private var TAG: String = InterCityAdapter::class.java.simpleName
    private val viewPool = RecyclerView.RecycledViewPool()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /* return ViewHolder(
             LayoutInflater.from(context).inflate(
                 R.layout.child_search_selection,
                 parent,
                 false
             )
         )*/

        val binding =
            InterCityAdapterChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList.get(position)
        holder.tvSearch.text = "${searchModel.name}"
        val finalstationList = arrayListOf<SearchModel>()


        holder.layoutSearchSelection.setOnClickListener {
            holder.layoutSearchSelection.tag = fromOrigin
            onItemPassData.onItemData(
                holder.layoutSearchSelection,
                searchModel.name.toString(),
                searchModel.id.toString()
            )
        }
        if (fromOrigin) {
            holder.boardingDroppingHeading.text = context.getString(R.string.boarding_point)
        } else {
            holder.boardingDroppingHeading.text = context.getString(R.string.dropping_point)
        }
        val tempVar = searchModel.id.toString()?.split(":")
        for (j in 0..stationList.size.minus(1)) {

            val tempstate = stationList[j].id.toString()?.split(":")
            if (tempstate!![1] == tempVar!![1]) {
                finalstationList.add(stationList[j])
            }

        }


        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val childSortSublistAdapter = InterStationAdapter(
            context,
            this,
            fromOrigin,
            finalstationList
        )
        layoutManager.initialPrefetchItemCount = searchList.size
        holder.rvStation.layoutManager = layoutManager
        holder.rvStation.adapter = childSortSublistAdapter
        holder.rvStation.setRecycledViewPool(viewPool)


    }

    fun filterList(filteredNames: MutableList<SearchModel>) {
        this.searchList = filteredNames
        notifyDataSetChanged()
    }


    class ViewHolder(binding: InterCityAdapterChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSearch = binding.tvSearch
        val layoutSearchSelection = binding.layoutSearchSelection
        val rvStation = binding.cityStation
        val boardingDroppingHeading = binding.boardingDroppingHeading
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        onItemPassData.onItemData(view, str1, str2)
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        TODO("Not yet implemented")
    }
}