package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ChildSearchSelectionBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel

class InterStationAdapter(
    private val context: Context,
    private val onItemPassData: OnItemPassData,
    private val fromOrigin: Boolean,
    private var searchList: ArrayList<SearchModel>
) :
    RecyclerView.Adapter<InterStationAdapter.ViewHolder>() {
    private var TAG: String = SearchAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /* return ViewHolder(
             LayoutInflater.from(context).inflate(
                 R.layout.child_search_selection,
                 parent,
                 false
             )
         )*/

        val binding =
            ChildSearchSelectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList.get(position)
        holder.tvSearch.text = "${searchModel.name}"

        holder.layoutSearchSelection.setOnClickListener {
            holder.layoutSearchSelection.tag = fromOrigin
            onItemPassData.onItemData(
                holder.layoutSearchSelection,
                searchModel.name.toString(),
                searchModel.id.toString()
            )
        }
    }

    fun filterList(filteredNames: ArrayList<SearchModel>) {
        this.searchList = filteredNames
        notifyDataSetChanged()
    }


    class ViewHolder(binding: ChildSearchSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvSearch = binding.tvSearch
        val layoutSearchSelection = binding.layoutSearchSelection
    }
}