package com.bitla.ts.presentation.adapter

import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import gone
import java.util.ArrayList

class SourceDestinatinAdapter(
    private val context: Context,
    private var sourceDestinationList: ArrayList<CitiesListData>,
    var listener: DialogButtonAnyDataListener,
    var type: Int,
) : RecyclerView.Adapter<SourceDestinatinAdapter.ViewHolder>(), Filterable {
    var sourceDestinationFilteredList: ArrayList<CitiesListData> = sourceDestinationList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterSimpleListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sourceDestinationFilteredList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = sourceDestinationFilteredList[position]
        holder.title.text = data.name

     /*   if (position + 1 == sourceDestinationFilteredList.size) {
            holder.bottomV.gone()
        }*/

        holder.title.setOnClickListener {
            listener.onDataSendWithExtraParam(1, sourceDestinationFilteredList[position], type)
        }
    }

    class ViewHolder(binding: AdapterSimpleListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleTV
        val bottomV = binding.bottomV
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                sourceDestinationFilteredList =
                    if (charString.isEmpty()) sourceDestinationList else {
                        val filteredList = ArrayList<CitiesListData>()
                        sourceDestinationList
                            .filter {
                                (it.name?.lowercase()?.startsWith(constraint.toString().lowercase()) == true)

                            }
                            .forEach { filteredList.add(it) }
                        filteredList

                    }

                return FilterResults().apply { values = sourceDestinationFilteredList }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                sourceDestinationFilteredList = if (results?.values == null)
                    ArrayList<CitiesListData>()
                else
                    results.values as ArrayList<CitiesListData>
                notifyDataSetChanged()
            }
        }
    }
}