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
import gone

class SimpleListAdapter(
    private val context: Context,
    private var bpDpList: MutableList<Origin>,
    var listener: DialogButtonAnyDataListener,
    var type: Int,
) : RecyclerView.Adapter<SimpleListAdapter.ViewHolder>(), Filterable {
    var bpDpFilteredList: MutableList<Origin> = bpDpList
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterSimpleListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun getItemCount(): Int {
        return bpDpFilteredList.size
    }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in bpDpFilteredList.indices) {

            val data = bpDpFilteredList[position]
            holder.title.text = data.name

            if (position + 1 == bpDpFilteredList.size) {
                holder.bottomV.gone()
            }

            holder.title.setOnClickListener {
                listener.onDataSendWithExtraParam(1, bpDpFilteredList[position], type)
            }
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
                bpDpFilteredList =
                    if (charString.isEmpty()) bpDpList else {
                        val filteredList = mutableListOf<Origin>()
                        bpDpList
                            .filter {
                                (it.name?.lowercase()
                                    ?.contains(constraint.toString().lowercase()) == true)
                                
                            }
                            .forEach { filteredList.add(it) }
                        filteredList
                        
                    }
                
                return FilterResults().apply { values = bpDpFilteredList }
            }
            
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                
                bpDpFilteredList = if (results?.values == null)
                    mutableListOf()
                else
                    results.values as MutableList<Origin>
                notifyDataSetChanged()
            }
        }
    }
}