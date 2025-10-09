package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.databinding.ListItemBinding
import com.bitla.restaurant_app.databinding.MealReportItemBinding
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.reports.Info
import com.bitla.ts.utils.common.convert


class MealReportAdapter(
    private val reports: ArrayList<Info>,
    private val currency: String,
    private val currencyFormat: String,
    private val context: Context
) :
    RecyclerView.Adapter<MealReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(view: MealReportItemBinding) : RecyclerView.ViewHolder(view.root) {
        val tvTimeAndService: TextView = view.tvTimeAndService
        val tvRoute: TextView = view.tvRoute
        val tvCouponCode: TextView = view.tvCouponCode
        val tvCouponAmount: TextView = view.tvCouponAmount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding =
            MealReportItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.apply {
            tvTimeAndService.text = "${report.depurtureTime} | ${report.serviceName}"
            tvRoute.text = "${report.origin} to ${report.destination}"
            tvCouponCode.text = report.mealCoupon
            tvCouponAmount.text =currency+ report.totalFare.toString().toDouble().convert(currencyFormat)
        }
    }
}