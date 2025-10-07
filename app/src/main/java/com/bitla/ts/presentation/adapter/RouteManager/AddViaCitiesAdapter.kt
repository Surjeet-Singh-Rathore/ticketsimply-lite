package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterBottomSheetAddViaCitiesBinding
import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import java.util.ArrayList


class AddViaCitiesAdapter(
    private val context: Context,
    var citiesList: ArrayList<CitiesListData>,
    var clickListener: DialogButtonAnyDataListener
    //private var list: ArrayList<String>

) :
    RecyclerView.Adapter<AddViaCitiesAdapter.ViewHolder>() , Filterable {
    var viaCityFilterList: ArrayList<CitiesListData> = citiesList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterBottomSheetAddViaCitiesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cityCB.isChecked = citiesList[position].isSelected

        holder.title.text = citiesList[position].name
        holder.cityCB.setOnClickListener {
            citiesList[position].isSelected = holder.cityCB.isChecked
            clickListener.onDataSend(1,citiesList)

        }






    }

    class ViewHolder(binding: AdapterBottomSheetAddViaCitiesBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var title : TextView = binding.cityTV
            var cityCB :CheckBox = binding.cityCB



    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                viaCityFilterList =
                    if (charString.isEmpty()) citiesList else {
                        val filteredList = ArrayList<CitiesListData>()
                        citiesList
                            .filter {
                                (it.name?.lowercase()
                                    ?.contains(constraint.toString().lowercase()) == true)

                            }
                            .forEach { filteredList.add(it) }
                        filteredList

                    }

                return FilterResults().apply { values = viaCityFilterList }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                viaCityFilterList = if (results?.values == null)
                    ArrayList<CitiesListData>()
                else
                    results.values as ArrayList<CitiesListData>
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: ArrayList<CitiesListData>) {
        citiesList = newList
        notifyDataSetChanged()
    }
}