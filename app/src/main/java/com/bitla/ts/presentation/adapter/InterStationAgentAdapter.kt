package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ChildSearchSelectionBinding
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.utils.constants.DROPPING_SELECTION
import com.bitla.ts.utils.sharedPref.AGENT_SELECTED_DROPPING_DETAIL
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.SELECTED_DROPPING_DETAIL

class InterStationAgentAdapter(
    private val context: Context,
    private val onItemPassData: OnItemPassData,
    private var droppingList: MutableList<DropOffDetail>
) :
    RecyclerView.Adapter<InterStationAgentAdapter.ViewHolder>() {
    private var TAG: String = SearchAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSearchSelectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return droppingList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val droppingPoint: DropOffDetail = droppingList[position]
        val droppingInfo = if(!droppingPoint.time.isNullOrEmpty())"${droppingPoint.time} . ${droppingPoint.name}" else droppingPoint.name
        holder.tvSearch.text = droppingInfo

        holder.layoutSearchSelection.setOnClickListener {
            holder.layoutSearchSelection.tag = DROPPING_SELECTION
            if (droppingPoint.default_stage_id != null) {
                val dropOffDetail = DropOffDetail(
                    address = "",
                    id = droppingPoint.default_stage_id,
                    landmark = "",
                    name = droppingPoint.name,
                    time = droppingPoint.time,
                    distance = ""
                )
                PreferenceUtils.putObject(dropOffDetail, SELECTED_DROPPING_DETAIL)
                PreferenceUtils.putObject(dropOffDetail, AGENT_SELECTED_DROPPING_DETAIL)
            }
            onItemPassData.onItemData(
                holder.layoutSearchSelection,
                droppingPoint.name,
                droppingPoint.id
            )
        }
    }

    fun filterList(filteredNames: ArrayList<DropOffDetail>) {
        this.droppingList = filteredNames
        notifyDataSetChanged()
    }


    class ViewHolder(binding: ChildSearchSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvSearch = binding.tvSearch
        val layoutSearchSelection = binding.layoutSearchSelection
    }
}