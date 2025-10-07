package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.LayoutLabelModifyFareBinding
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.CmsnDetail
import firstLetterWord

class ModifyCommissionUpperLabelAdapter(
    private val context: Context,
    private var cmsnDetailList: MutableList<CmsnDetail>,
) :
    RecyclerView.Adapter<ModifyCommissionUpperLabelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutLabelModifyFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cmsnDetailList.size
    }

    @SuppressLint("RtlHardcoded", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item: CmsnDetail = cmsnDetailList[position]
        holder.fare.text = "${item.cmsn}"

        holder.title.text = "${firstLetterWord(item.seatType)}: "

//        if (item.seatType == "Lower Berth")
//            holder.title.text = "LB: "
//        if (item.seatType == "Upper Berth")
//            holder.title.text = "UB: "
//        if (item.seatType == "Side Lower Berth")
//            holder.title.text = "SLB"
//        if (item.seatType == "Side Upper Berth")
//            holder.title.text = "SUB: "
//        if (item.seatType == "Double Lower Berth")
//            holder.title.text = "DLB: "
//        if (item.seatType == "Double Upper Berth")
//            holder.title.text = "DUB: "
//        if (item.seatType == "Semi Sleeper")
//            holder.title.text = "SS: "
        if (item.seatType == "Seater")
            holder.title.text = "SE: "
        if (item.seatType == "Sleeper")
            holder.title.text = "SL: "


        item.cmsn.let {
            holder.setSeatFare(it.toString())
        }

    }

    inner class ViewHolder(binding: LayoutLabelModifyFareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvSeatName
        val fare = binding.tvSeatFare

        fun setSeatFare(seatFare: CharSequence?) {
            fare.text = seatFare
        }

    }
}