package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterModifyIndividualRouteFareBinding
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.CityWiseCmsn
import gone

class ModifyCommissionDetailsAdapter(
    private val context: Context,
    private var routeWiseCmsnDetailsList: MutableList<CityWiseCmsn>,


    ) :
    RecyclerView.Adapter<ModifyCommissionDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterModifyIndividualRouteFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return routeWiseCmsnDetailsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val fareDetailsResponse: CityWiseCmsn = routeWiseCmsnDetailsList[position]
        val fareList = fareDetailsResponse.cmsnDetails

//        ---------Header----------
        holder.titleTV.text = "${fareDetailsResponse.originName} - ${fareDetailsResponse.destinationName}"
        val isExpandable = fareDetailsResponse.isExpandable
        holder.arrowIV.gone()

//        if (isExpandable) {
//            holder.arrowIV.setImageResource(R.drawable.ic_arrow_up)
//            holder.rvNestedUpper.gone()
//            holder.rvNestedDown.visible()
//        } else {
//            holder.arrowIV.setImageResource(R.drawable.ic_arrow_down)
//            holder.rvNestedUpper.visible()
//            holder.rvNestedDown.gone()
//        }


        //        ----------Upper Layout---------
//        val modifyUpperLabelSeatFareAdapter = ModifyCommissionUpperLabelAdapter(
//            context = context,
//            cmsnDetailList = fareList,
//        )
//        val layoutManagerHorizontal = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        layoutManagerHorizontal.initialPrefetchItemCount = fareList.size
//        holder.rvNestedUpper.layoutManager = layoutManagerHorizontal
//        holder.rvNestedUpper.adapter = modifyUpperLabelSeatFareAdapter


        //        ----------Down Layout---------
        val modifyDownSeatFareValueAdapter = ModifyCommissionDetailsDownAdapter(
            context = context,
            cmsnDetailList = fareList,
        )

        val layoutManagerGrid = GridLayoutManager(context,2)
        layoutManagerGrid.initialPrefetchItemCount = fareList.size
        holder.rvNestedDown.layoutManager = layoutManagerGrid
        holder.rvNestedDown.adapter = modifyDownSeatFareValueAdapter


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