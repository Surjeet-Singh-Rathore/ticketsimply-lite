package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildExtraSeatsBinding
import com.bitla.ts.domain.pojo.service_details_response.ExtraSeatDetail
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible

class ExtraSeatAdapter(
    private val context: Context,
    private var extraSeatList: List<ExtraSeatDetail>,
    private val currency: String,
    private val currencyFormat: String,
    private val country: String,
    private val showExtraSeatsOption: (view: View, position: Int) -> Unit,
) :
    RecyclerView.Adapter<ExtraSeatAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildExtraSeatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return extraSeatList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val extraSeatDetail: ExtraSeatDetail = extraSeatList[position]
        val colorString = extraSeatDetail.backgroundColor
        var color: Int
        var textColor:Int
        if (country.equals("India", true)) {
            try {
                color = Color.parseColor(colorString)
                textColor = ContextCompat.getColor(context, android.R.color.black)
            } catch (e: Exception) {
                color = ContextCompat.getColor(context, R.color.primaryLight)
                textColor = ContextCompat.getColor(context, R.color.project_light_blue)
            }
        } else {
            color = ContextCompat.getColor(context, R.color.primaryLight)
            textColor = ContextCompat.getColor(context, R.color.project_light_blue)
        }


        if (extraSeatDetail.seatNo != null && extraSeatDetail.seatNo!!.isNotEmpty()) {
//            if (extraSeatDetail.seatNo!!.split("-")[1].isNotEmpty())
//                holder.tvSeatNo.text = extraSeatDetail.seatNo!!.split("-")[1]
            holder.tvSeatNo.text = extraSeatDetail.seatNo!!
        }
        if (extraSeatDetail.bookingFare != null && !extraSeatDetail.bookingFare.isNullOrEmpty()) {
            holder.tvExtraSeatFare.visible()
            holder.apply {
                parentLL.backgroundTintList = ColorStateList.valueOf(color)
                tvSeatNo.setTextColor(textColor)
                tvExtraSeatFare.setTextColor(textColor)
                statusTV.setTextColor(textColor)
            }

            var seatFare = ""
            if(!currency.isNullOrEmpty()){
                 seatFare = "$currency ${
                    extraSeatDetail?.bookingFare.let {
                        (it?.toDouble())?.convert(currencyFormat)
                    }
                }"
            }

            holder.tvExtraSeatFare.text = seatFare
        } else
            holder.tvExtraSeatFare.gone()





        if (extraSeatDetail.status==2) {
            holder.statusTV.visible()
            holder.statusTV.text = holder.statusTV.text.toString() + "✔"
        } else {
            holder.statusTV.text = holder.statusTV.text.toString()
        }

        if (extraSeatDetail.remarks!=null && extraSeatDetail.remarks!="") {
            holder.statusTV.visible()
            holder.statusTV.text = holder.statusTV.text.toString()+ "*"
        } else {
            holder.statusTV.text = holder.statusTV.text.toString()
        }

        holder.itemView.setOnClickListener {
            if(!PreferenceUtils.getLogin().role.equals("Field Officer",true)){
                showExtraSeatsOption.invoke(it, position)
            }
        }

    }

    class ViewHolder(binding: ChildExtraSeatsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSeatNo = binding.tvSeatNo
        val statusTV = binding.statusTV
        val parentLL = binding.parentLLayout
        val tvExtraSeatFare = binding.tvExtraSeatFare
    }
}