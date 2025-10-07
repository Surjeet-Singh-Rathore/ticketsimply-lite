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
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.utils.sharedPref.PREF_BOARDING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_DROPPING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PreferenceUtils


class BoardingListAdapter(
    private val context: Context,
    private var onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<BoardingListAdapter.ViewHolder>(), Filterable {
    private var lastSelectedPosition: Int = -1
    private var lastSelectedId: Int = -1
    var boardingDropping = mutableListOf<StageDetail>()
    var boardingDroppingFiltered = mutableListOf<StageDetail>()


    companion object {
        var tag: String? = BoardingListAdapter::class.simpleName
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
//        val stageDetail: StageDetail = boardingDropping[position]
        var stageDetail = boardingDroppingFiltered[position]

        /*val timeWithAddress =
            "${stageDetail.time} - ${stageDetail.name}\n ${stageDetail.address}, ${stageDetail.city} "
        */
        /*val timeWithAddress =
            "${stageDetail.time} \u2022 ${stageDetail.name} ${stageDetail.address}, ${stageDetail.city} "
*/
        val timeWithAddress: String = if (stageDetail.address.isNullOrEmpty()) {
            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.city}"
        } else {
            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.address}, ${stageDetail.city}"
        }

        holder.radioItem.text = timeWithAddress
        val boardingPointId =
            PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.id
        val droppingPointId =
            PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.id
        holder.radioItem.setOnCheckedChangeListener(null)
        if (boardingDropping.size == 1) {
            lastSelectedPosition = position
            lastSelectedId = stageDetail.id ?: -1
        } else {
            if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
                val boardingPointId =
                    PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.id
                if (stageDetail.id == boardingPointId) {
                    lastSelectedPosition = position
                    lastSelectedId = stageDetail.id ?: -1
                }
            }
            if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
                val droppingPointId =
                    PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.id
                if (stageDetail.id == droppingPointId) {
                    lastSelectedPosition = position

                    lastSelectedId = stageDetail.id ?: -1
                }
            }
//            Timber.d("$tag boardingPointId==> $boardingPointId droppingPointId $droppingPointId")
//            Timber.d("$tag boardingPointId==> ${boardingDropping[position].name} droppingPointId ${boardingDropping[position].id}")

        }


//        if(boardingDropping[position].id == boardingPointId || boardingDropping[position].id== droppingPointId){
//            //holder.radioItem.isChecked = true
//            //lastSelectedPosition=position
//
////            Timber.d("$tag boardingPointId==> $boardingPointId droppingPointId $droppingPointId")
//
//            //onItemClickListener.onClick(holder.radioItem,position)
//        }
        //else {
        holder.radioItem.isChecked = stageDetail.id == lastSelectedId
        holder.radioItem.setOnClickListener {
            it.tag = tag
            lastSelectedPosition = position
            lastSelectedId = stageDetail.id ?: -1
            notifyDataSetChanged()
            onItemClickListener.onClick(it, stageDetail.id ?: -1)

        }
        //}

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

    fun addData(serviceList: MutableList<StageDetail>) {
        boardingDropping = serviceList
        boardingDroppingFiltered = serviceList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                boardingDroppingFiltered =
                    if (charString.isEmpty()) boardingDropping else {
                        val filteredList = mutableListOf<StageDetail>()
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
                    results.values as MutableList<StageDetail>
                notifyDataSetChanged()
            }
        }
    }

}