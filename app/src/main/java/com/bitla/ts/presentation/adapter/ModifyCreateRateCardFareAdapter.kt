package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterModifyIndividualRouteFareBinding
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareDetail
import gone
import visible

class ModifyCreateRateCardFareAdapter(
    private val context: Context,
    private var routeWiseFareDetailList: MutableList<FetchRouteWiseFareDetail>,
    private val onItemClickListener: OnItemClickListener,
    private var onFareChangeParent: ((item: FetchRouteWiseFareDetail) -> Unit)

) :
    RecyclerView.Adapter<ModifyCreateRateCardFareAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterModifyIndividualRouteFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return routeWiseFareDetailList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val fareDetailsResponse: FetchRouteWiseFareDetail = routeWiseFareDetailList[position]
        val fareList = fareDetailsResponse.fareDetails

//        ---------Header----------
        holder.titleTV.text = "${fareDetailsResponse.originName} - ${fareDetailsResponse.destinationName}"
        val isExpandable = fareDetailsResponse.isExpandable

        if (isExpandable) {
            holder.arrowIV.setImageResource(R.drawable.ic_arrow_up)
            holder.rvNestedUpper.gone()
            holder.rvNestedDown.visible()
        } else {
            holder.arrowIV.setImageResource(R.drawable.ic_arrow_down)
            holder.rvNestedUpper.visible()
            holder.rvNestedDown.gone()
        }

        //        ----------Upper Layout---------
        val modifyUpperLabelSeatFareAdapter = ModifyCreateRateCardUpperLabelAdapter(
            context = context,
            fareDetailsList = fareList,
        )
        val layoutManagerHorizontal = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerHorizontal.initialPrefetchItemCount = fareList.size
        holder.rvNestedUpper.layoutManager = layoutManagerHorizontal
        holder.rvNestedUpper.adapter = modifyUpperLabelSeatFareAdapter
//        holder.rvNestedUpper.setRecycledViewPool(viewPoolUpper)

        //        ----------Down Layout---------
        val modifyDownSeatFareValueAdapter = ModifyCreateRateCardDownAdapter(
            context = context,
            fareDetailsList = fareList,
            position,
        ) { item, childPosition ->

            if (position == 0) {
                fareDetailsResponse.fareDetails[childPosition] = item
                onFareChangeParent.invoke(fareDetailsResponse)
            }
        }
        val layoutManagerGrid = GridLayoutManager(context, 2)
        layoutManagerGrid.initialPrefetchItemCount = fareList.size
        holder.rvNestedDown.layoutManager = layoutManagerGrid
        holder.rvNestedDown.adapter = modifyDownSeatFareValueAdapter
//        holder.rvNestedDown.setRecycledViewPool(viewPoolDown)

        holder.arrowIV.setOnClickListener {
            fareDetailsResponse.isExpandable = !fareDetailsResponse.isExpandable
            notifyItemChanged(holder.absoluteAdapterPosition)
        }
    }

    class ViewHolder(binding: AdapterModifyIndividualRouteFareBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleTV: TextView = binding.titleTV
        val arrowIV: ImageView = binding.arrowIV
        val rvNestedUpper = binding.rvFareUpper
        val rvNestedDown = binding.rvFareDown
    }
}