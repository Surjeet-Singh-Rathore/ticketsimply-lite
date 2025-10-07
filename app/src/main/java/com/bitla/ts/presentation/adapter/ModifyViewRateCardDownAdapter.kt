package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.LayoutViewModifiedFareValueBinding
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.FareDetail
import firstLetterWord

class ModifyViewRateCardDownAdapter(
    private val context: Context,
    private var fareDetailsList: MutableList<FareDetail>,
    private val parentPosition: Int?,
    private var onFareChangeChild: ((item: FareDetail, position: Int) -> Unit),
) :
    RecyclerView.Adapter<ModifyViewRateCardDownAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutViewModifiedFareValueBinding.inflate(LayoutInflater.from(context), parent, false)
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

        holder.title.text = "${firstLetterWord(item.seatType.toString())}(₹)"
        if (item.seatType == "Seater")
            holder.title.text = "SE(₹)"
        if (item.seatType == "Sleeper")
            holder.title.text = "SL(₹)"


//        item.fare.let {
//            holder.setSeatFare(it)
//        }

//        holder.fare.addTextChangedListener(object :
//            TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {}
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(
//                s: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int,
//            ) {
//                try {
//                    item.fare = s.toString()
//                    if (parentPosition == 0) {
//                        item.editedFare = s.toString()
//                        onFareChangeChild.invoke(item, holder.absoluteAdapterPosition)
//                    }
//                } catch (ex: NumberFormatException) {
//                }
//            }
//        })
    }

    inner class ViewHolder(binding: LayoutViewModifiedFareValueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleTV
        val fare = binding.tvSeatFare

        fun setSeatFare(seatFare: CharSequence?) {
            fare.text = seatFare
        }

    }
}