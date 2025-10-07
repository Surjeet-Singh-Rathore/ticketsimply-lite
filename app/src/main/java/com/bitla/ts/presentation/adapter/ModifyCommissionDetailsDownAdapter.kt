package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.LayoutViewModifiedFareValueBinding
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.CmsnDetail
import firstLetterWord

class ModifyCommissionDetailsDownAdapter(
    private val context: Context,
    private var cmsnDetailList: MutableList<CmsnDetail>,
) :
    RecyclerView.Adapter<ModifyCommissionDetailsDownAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutViewModifiedFareValueBinding.inflate(LayoutInflater.from(context), parent, false)
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
        holder.title.text = "${firstLetterWord(item.seatType.toString())}(₹)"

        if (item.seatType == "Seater")
            holder.title.text = "SE(₹)"
        if (item.seatType == "Sleeper")
            holder.title.text = "SL(₹)"


//        item.cmsn.let {
//            holder.setSeatFare(it.toString())
//        }
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