package com.bitla.ts.presentation.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCityListBinding
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible

class CityListAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: Map<Int?, String?>


) :
    RecyclerView.Adapter<CityListAdapter.ViewHolder>() {
    private var TAG: String = CityListAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /* return ViewHolder(
             LayoutInflater.from(context).inflate(
                 R.layout.child_search_selection,
                 parent,
                 false
             )
         )*/

        val binding =
            ChildCityListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val searchModel: String = searchList.getValue(position+1)
        val preforigin = PreferenceUtils.getPreference("selectedCityOrigin", "")
        val prefdestination = PreferenceUtils.getPreference("selectedCityDestination", "")
        val value = arrayListOf<String?>()
        val keys = arrayListOf<Int?>()
        value.addAll(searchList.values)
        keys.addAll(searchList.keys)
        when (value[position]) {
            preforigin -> {
                holder.tickedCity.visible()
            }
            prefdestination -> {
                holder.tickedCity.visible()

            }
            else -> {
                holder.tickedCity.gone()
            }
        }

        holder.tvCityName.text = value[position]


        holder.layoutSearchSelection.setOnClickListener {
            value[position]?.let { it1 ->
                keys[position]?.let { it2 ->
                    onItemClickListener.onClickOfItem(it1, it2)
                }
            }

        }
    }

//    fun filterList(filteredNames: ArrayList<CityDetailsResponseModelItem>) {
//        this.searchList = filteredNames
//        notifyDataSetChanged()
//    }


    class ViewHolder(binding: ChildCityListBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCityName = binding.cityName
        val tickedCity = binding.selectedTick
        val layoutSearchSelection = binding.layoutnames
    }
}