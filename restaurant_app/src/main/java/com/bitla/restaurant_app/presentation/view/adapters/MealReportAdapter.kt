package com.bitla.restaurant_app.presentation.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.presentation.pojo.reports.Info
import com.bitla.restaurant_app.presentation.utils.convert

class MealReportAdapter(
    private val reports: ArrayList<Info>,
    private val currency: String,
    private val currencyFormat: String
) :
    RecyclerView.Adapter<MealReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTimeAndService: TextView = view.findViewById(R.id.tvTimeAndService)
        val tvRoute: TextView = view.findViewById(R.id.tvRoute)
        val tvCouponCode: TextView = view.findViewById(R.id.tvCouponCode)
        val tvCouponAmount: TextView = view.findViewById(R.id.tvCouponAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.meal_report_item, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.apply {
            tvTimeAndService.text = report.depurtureTime +" | "+report.serviceName
            tvRoute.text = report.origin +" to "+report.destination
            tvCouponCode.text = report.mealCoupon
            tvCouponAmount.text =currency+ report.totalFare.toString().toDouble().convert(currencyFormat)
        }
    }
}