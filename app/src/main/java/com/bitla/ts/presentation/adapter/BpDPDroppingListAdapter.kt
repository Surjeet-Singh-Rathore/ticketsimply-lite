package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildBoardingListAdapterBinding
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.utils.sharedPref.*
import timber.log.Timber


class BpDPDroppingListAdapter(
    private val context: Context,
    private var boardingDropping: MutableList<DropOffDetail>,
    private var onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<BpDPDroppingListAdapter.ViewHolder>(), Filterable {
    private var lastSelectedPosition: Int = -1
    private var lastSelectedId: Int = -1
    private var boardingDroppingFiltered = boardingDropping

    companion object {
        var tag: String? = BpDPDroppingListAdapter::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildBoardingListAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return boardingDroppingFiltered.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        //holder.radioItem.isChecked = lastSelectedPosition == position
        val stageDetail: DropOffDetail = boardingDroppingFiltered[position]
        Timber.d("selectedDropping0: ${PreferenceUtils.getString("preselectedDPData")}")
//

        val selectedDropping = PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)
        Timber.d("selectedDropping02: ${selectedDropping}")


        /*val timeWithAddress =
            "${stageDetail.time} - ${stageDetail.name}\n ${stageDetail.address}, ${stageDetail.city} "
        */
        /*val timeWithAddress =
            "${stageDetail.time} \u2022 ${stageDetail.name} ${stageDetail.address}, ${stageDetail.city} "
*/
        val timeWithAddress: String = if (stageDetail.address.isNullOrEmpty()) {
            "${stageDetail.time} • ${stageDetail.name}"
//            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.city}"
        } else {
            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.address}"
//            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.address}, ${stageDetail.city}"
        }

//        if (dpSeleted== "true"){
//            val itemBp= PreferenceUtils.getObject<DropOffDetail>(PRE_SELECTED_DROPPING_DETAIL)
//            if (stageDetail==itemBp){
//                holder.radioItem.tag= tag
//                lastSelectedPosition= position
//                onItemClickListener.onClick(holder.radioItem, lastSelectedPosition)
//                PreferenceUtils.putString("preselectedDPData", "false")
//            }
//        }
//        Timber.d("selectedDropping0: ${selectedDropping}== ${stageDetail.id}")
//
        val isPickupDropoffChargesEnabled = PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false) ?: false
        if (selectedDropping?.id == "" && !isPickupDropoffChargesEnabled) {
            lastSelectedPosition = -1
            lastSelectedId = -1
        } else {
            if (selectedDropping?.id == stageDetail.id) {
                lastSelectedPosition = position
                lastSelectedId = stageDetail.id.toInt()
            }
        }


        Timber.d("boardingDroppiingCheck:::${boardingDropping.size}")
        if (boardingDropping.size == 1) {
            lastSelectedPosition = position
            lastSelectedId = stageDetail.id.toInt()
        }

        holder.radioItem.text = timeWithAddress
        val boardingPointId =
            PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.id
        val droppingPointId =
            PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.id
        holder.radioItem.setOnCheckedChangeListener(null)

//        if(boardingDropping[position].id == boardingPointId || boardingDropping[position].id== droppingPointId){
//            //holder.radioItem.isChecked = true
//            //lastSelectedPosition=position
//
//            Timber.d("$tag boardingPointId==> $boardingPointId droppingPointId $droppingPointId")
//
//            //onItemClickListener.onClick(holder.radioItem,position)
//        }
        //else {

        holder.radioItem.isChecked = stageDetail.id.toInt() == lastSelectedId
        holder.radioItem.setOnClickListener {
            it.tag = tag
            lastSelectedPosition = position
            lastSelectedId = stageDetail.id.toInt()

            notifyDataSetChanged()
            onItemClickListener.onClick(it, stageDetail.id.toInt())

        }

        //}

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                boardingDroppingFiltered =
                    if (charString.isEmpty()) boardingDropping else {
                        val filteredList = mutableListOf<DropOffDetail>()
                        boardingDropping
                            .filter {
                                (it.name?.lowercase()
                                    ?.contains(constraint.toString().lowercase()) == true)

                            }
                            .forEach { filteredList.add(it) }
                        filteredList

                    }

                return FilterResults().apply { values = boardingDroppingFiltered }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                boardingDroppingFiltered = if (results?.values == null)
                    mutableListOf()
                else
                    results.values as MutableList<DropOffDetail>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(binding: ChildBoardingListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val radioItem = binding.radioItem

        init {
            radioItem.setOnClickListener {
                radioItem.tag = tag
                lastSelectedPosition = adapterPosition
                lastSelectedId = -1

                notifyDataSetChanged()
                //onItemClickListener.onClick(radioItem, lastSelectedPosition)
            }
        }
    }
}