package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.LayoutLabelModifyFareBinding
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.FareDetail
import firstLetterWord

class ModifyCreateRateCardUpperLabelAdapter(
    private val context: Context,
    private var fareDetailsList: MutableList<FareDetail>,
) :
    RecyclerView.Adapter<ModifyCreateRateCardUpperLabelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutLabelModifyFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fareDetailsList.size
    }

    @SuppressLint("RtlHardcoded", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item: FareDetail = fareDetailsList[position]
        holder.fare.text = item.fare

        holder.title.text = "${firstLetterWord(item.seatType.toString())}: "

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


        item.fare.let {
            holder.setSeatFare(it.toString())
        }

        holder.fare.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                try {
                    item.fare = s.toString()
                } catch (ex: NumberFormatException) {
                }
            }
        })
    }

    inner class ViewHolder(binding: LayoutLabelModifyFareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvSeatName
        val fare = binding.tvSeatFare

        fun setSeatFare(seatFare: CharSequence?) = fare.setText(seatFare)

    }
}