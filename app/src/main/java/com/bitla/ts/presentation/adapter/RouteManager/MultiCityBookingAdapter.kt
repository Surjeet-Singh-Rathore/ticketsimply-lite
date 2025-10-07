package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterMultiCityBookingBinding
import com.bitla.ts.domain.pojo.city_pair.Result
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import gone
import visible

class MultiCityBookingAdapter(
    private val context: Context,
    private var resultDataList: MutableList<Result>,
    private var listener: DialogAnyClickListener,
    var viewModel: RouteManagerViewModel<Any?>,
    private var onFareChangeParent: (item: Result) -> Unit

) :
    RecyclerView.Adapter<MultiCityBookingAdapter.ViewHolder>(), DialogAnyClickListener {
    

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterMultiCityBookingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultDataList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val cityPairsList: Result = resultDataList[position]
        
        val fareList = cityPairsList.fareDetails

        try {

            if(viewModel.isEdit.value == true){
                var orgId = viewModel.getRouteData.value?.peekContent()?.result?.basicDetails?.originId
                var desId = viewModel.getRouteData.value?.peekContent()?.result?.basicDetails?.destId
                holder.checkboxCB.isEnabled = !(cityPairsList.originId == orgId?.toInt() && cityPairsList.destinationId == desId?.toInt())

            }else{
                var originId = viewModel.routeJsonObject.value?.getAsJsonObject("basic_details")?.get("origin_id")
                var destId = viewModel.routeJsonObject.value?.getAsJsonObject("basic_details")?.get("dest_id")
                holder.checkboxCB.isEnabled = !(cityPairsList.originId == originId.toString().toInt() && cityPairsList.destinationId == destId.toString().toInt())

            }


        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }


        holder.binding.cityPairTV.text = cityPairsList.originName + " - " +cityPairsList.destinationName

        // ----Upper Adapter----
        val seatFareAdapter = ShowFareUpperAdapter(
            context = context,
            fareDetailList = fareList
        )
        
        val layoutManagerHorizontal = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerHorizontal.initialPrefetchItemCount = fareList.size
        holder.fareRecyclerViewUpper.layoutManager = layoutManagerHorizontal
        holder.fareRecyclerViewUpper.adapter = seatFareAdapter
        
        // ----Down Adapter----
       val  updateFareDownAdapter = UpdateFareDownAdapter(
           context = context,
           fareDetailList = fareList,
           parentPosition = position
           
       ){ item, childPosition ->
           if (position == 0) {
               cityPairsList.fareDetails[childPosition] = item
               onFareChangeParent.invoke(cityPairsList)
           }
       }
        
        val layoutManagerGrid = GridLayoutManager(context, 2)
        layoutManagerGrid.initialPrefetchItemCount = fareList.size
        holder.fareRecyclerViewDown.layoutManager = layoutManagerGrid
        holder.fareRecyclerViewDown.adapter = updateFareDownAdapter
        
        
        holder.editFareButton.setOnClickListener{
            holder.fareGroup.gone()
            holder.updateFareGroup.visible()
        }
        
        holder.checkboxCB.setOnClickListener {
            cityPairsList.isChecked = holder.checkboxCB.isChecked
        }
        
        holder.saveButton.setOnClickListener{
            holder.updateFareGroup.gone()
            holder.fareGroup.visible()
            notifyItemChanged(holder.absoluteAdapterPosition)
            
        }
        
        holder.binding.checkBoxCB.isChecked = cityPairsList.isChecked

    }

    class ViewHolder(val binding: AdapterMultiCityBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val fareRecyclerViewUpper = binding.fareRVUpper
            val fareRecyclerViewDown = binding.fareRVDown
            val editFareButton = binding.editIV
            val fareGroup = binding.showFareG
            val updateFareGroup = binding.updateFareG
            val saveButton = binding.saveTV
            val checkboxCB = binding.checkBoxCB
        
    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        when(type){
            1 -> {
                resultDataList[position].fareDetails[position].fare = view.toString()
            }
        }

    }

    override fun onAnyClickListenerWithExtraParam(
        type: Int,
        view: Any,
        list: Any,
        position: Int,
        outPos: Int
    ) {

    }


    fun onFilterApplied(filterList: MutableList<Result>){
        resultDataList=filterList
        notifyDataSetChanged()
    }
}