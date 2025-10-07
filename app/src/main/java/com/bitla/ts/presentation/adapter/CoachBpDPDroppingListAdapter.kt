package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildBoardingListAdapterBinding
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.utils.sharedPref.PREF_BOARDING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_DROPPING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import timber.log.Timber


class CoachBpDPDroppingListAdapter(
    private val context: Context,
    private var boardingDropping: MutableList<StageDetail>,
    private var onItemClickListener: OnItemClickListener,
    private var selectedItem: DropOffDetail,
) :
    RecyclerView.Adapter<CoachBpDPDroppingListAdapter.ViewHolder>() {
    private var lastSelectedPosition: Int = -1

    companion object {
        var tag: String? = CoachBpDPDroppingListAdapter::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildBoardingListAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return boardingDropping.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val stageDetail: StageDetail = boardingDropping[position]

        val timeWithAddress: String = if (stageDetail.address.isNullOrEmpty()) {
//            "${stageDetail.time} • ${stageDetail.name}"
            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.city}"
        } else {
//            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.address}"
            "${stageDetail.time} • ${stageDetail.name}\n${stageDetail.address}, ${stageDetail.city}"
        }
        Timber.d("dialogCheckTest: ${stageDetail.id}:: ${selectedItem.id}")


        holder.radioItem.text = timeWithAddress
        val boardingPointId =
            PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.id
        val droppingPointId =
            PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.id
        holder.radioItem.setOnCheckedChangeListener(null)
//        if (selectedItem.id== stageDetail.id){
//            lastSelectedPosition= position
//        }
        if (lastSelectedPosition == -1) {
            if (selectedItem.id.toString() == stageDetail.id.toString()) {
                lastSelectedPosition = position
            }
        }
        holder.radioItem.isChecked = position == lastSelectedPosition
        holder.radioItem.setOnClickListener {
            it.tag = "Dropping"
            lastSelectedPosition = position
            notifyDataSetChanged()
            onItemClickListener.onClick(it, lastSelectedPosition)

        }

    }

    inner class ViewHolder(binding: ChildBoardingListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val radioItem = binding.radioItem

        init {
            radioItem.setOnClickListener {

                it.tag = "Dropping"

                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()
                onItemClickListener.onClick(radioItem, lastSelectedPosition)
            }
        }
    }

//    holder.radioItem.setOnClickListener {
//        if (isBoarding){
//            holder.radioItem.tag = "Boarding"
//        }else{
//            holder.radioItem.tag = "Dropping"
//        }
//        holder.radioItem.isChecked=true
//        onItemClickListener.onClick(holder.radioItem, position)
//    }

}