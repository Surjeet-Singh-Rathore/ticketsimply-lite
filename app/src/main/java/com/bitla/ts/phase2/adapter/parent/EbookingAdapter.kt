package com.bitla.ts.phase2.adapter.parent



import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterSummaryBookingBinding
import com.bitla.ts.domain.pojo.booking_summary_details.Detail
import com.bitla.ts.presentation.view.activity.ticketDetails.BookingSummaryActivity

class EbookingAdapter(
    private val context: Context?,
    private val EbookingDetailsList: List<Detail>,

    // private val privilegeResponseModel: PrivilegeResponseModel?,
    private val onItemClickListener: BookingSummaryActivity,
    private val isCardClickable: Boolean = true
) :
    RecyclerView.Adapter<EbookingAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSummaryBookingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }



    override fun getItemCount(): Int {
        return EbookingDetailsList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingSummaryDetailsData = EbookingDetailsList[position]

//var i = 0;

        holder.AgentName.text = bookingSummaryDetailsData.name

        holder.seatsCount.text = bookingSummaryDetailsData.seatCount


    }

    class ViewHolder(binding: AdapterSummaryBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // val rightArrowIV = binding.summaryImg
        val AgentName = binding.titleTV
        val seatsCount = binding.title1TV
        //val cardL = binding.card1
        //val revenueAmount = binding.title2TV

    }
}