package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface.BOLD
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterSummaryStagingBinding
import com.bitla.ts.domain.pojo.stage_summary_details.BoardingStageDetails


class BoardingStagingAdapter(
    private val context: Context?,
    private val boardingDetailsList: ArrayList<BoardingStageDetails>

) :
    RecyclerView.Adapter<BoardingStagingAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSummaryStagingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return boardingDetailsList.size
    }
private fun stringToSpan(totalSeatsToSpan : String, totalSeatsCount:String): Spannable {
    val totalSeatsToSpan: Spannable =
        SpannableString(totalSeatsToSpan)

    totalSeatsToSpan.setSpan(
        StyleSpan(BOLD),
        context!!.getString(R.string.total_seats_staging).length,
        context.getString(R.string.total_seats_staging).length + totalSeatsCount.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );
    totalSeatsToSpan.setSpan(
        ForegroundColorSpan(
            ContextCompat.getColor(
                context!!,
                R.color.colorDimShadow
            )
        ),
        0,
        context.getString(R.string.total_seats_staging).length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );
    return totalSeatsToSpan
}
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stagingSummaryDetailsData = boardingDetailsList[position]



        holder.BusStopName.text = stagingSummaryDetailsData.boarding_point_name.toString()

        val totalSeatsCount = "${stagingSummaryDetailsData.total_seats?.count()} - "

        val totalSeats =
            context?.getString(R.string.total_seats_staging) + totalSeatsCount + stagingSummaryDetailsData.total_seats.joinToString(", ")
        var totalSeatsToSpan = stringToSpan(totalSeats,totalSeatsCount)


        holder.seats.text = totalSeatsToSpan

    }

    class ViewHolder(binding: AdapterSummaryStagingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val BusStopName = binding.titleTVbusstop
        val seats = binding.title1TVSeats


    }
}