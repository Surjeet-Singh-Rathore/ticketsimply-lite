package com.bitla.ts.phase2.adapter.child

import android.annotation.*
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.*
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildPerformanceSummaryItemBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.PerformanceSummaryModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class PerformanceSummaryAdapter(
    private val context: Context,
    private var performanceSummaryModel: MutableList<PerformanceSummaryModel>,
    val privilegeResponse: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<PerformanceSummaryAdapter.ViewHolder>() {

    private var currency = privilegeResponse?.currency ?: ""
    private var currencyFormat = privilegeResponse?.currencyFormat ?: context.getString(R.string.indian_currency_format)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildPerformanceSummaryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return performanceSummaryModel.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val performanceSummaryData: PerformanceSummaryModel = performanceSummaryModel[position]
        
        if (position % 2 == 0) {
            holder.containerCardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.light_blue_card_background
                )
            )
        }
        
        holder.tvSource.text = performanceSummaryData.source
        holder.tvSeatsSold.text = performanceSummaryData.seatsSold

        val newGrossValue = performanceSummaryData.revenueGross.toString().replace(currency, "")
        val newNetRev = performanceSummaryData.revenueNet.toString().replace(currency, "")

        holder.tvGrossRevenue.text = "Gross : $currency" + newGrossValue.toDouble().convert(currencyFormat)
        holder.tvNetRevenue.text = "Net : $currency" + newNetRev.toDouble().convert(currencyFormat)
        //holder.tvGrossRevenue.text = "Gross : ₹${performanceSummaryData.revenueGross.toString().toBigDecimal().toPlainString()}"
        //holder.tvNetRevenue.text = "Net : ₹${performanceSummaryData.revenueNet.toString().toBigDecimal().toPlainString() }"

    }

    class ViewHolder(binding: ChildPerformanceSummaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSource = binding.tvSource
        val tvSeatsSold = binding.tvSeatsSold
        val tvGrossRevenue = binding.tvGrossRevenue
        val tvNetRevenue = binding.tvNetRevenue
        val containerCardView = binding.cardView
    }
}