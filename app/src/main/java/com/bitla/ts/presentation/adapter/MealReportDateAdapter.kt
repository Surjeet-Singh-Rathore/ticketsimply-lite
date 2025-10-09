package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.databinding.MealReportDateItemBinding
import com.bitla.restaurant_app.databinding.MealReportItemBinding
import com.bitla.restaurant_app.presentation.utils.gone
import com.bitla.restaurant_app.presentation.utils.visible
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.reports.ReportData

class MealReportDateAdapter(
    private var reportList: ArrayList<ReportData>,
    private val currency: String,
    private val currencyFormat: String,
    private val context:Context
) :
    RecyclerView.Adapter<MealReportDateAdapter.DateViewHolder>() {


    inner class DateViewHolder(view: MealReportDateItemBinding) : RecyclerView.ViewHolder(view.root) {
        val tvDate: TextView = view.tvDate
        val mealReportRecyclerView: RecyclerView = view.mealReportRecyclerView
        val showHide: ImageView = view.showHide
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = MealReportDateItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return DateViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val data = reportList[position]
        holder.apply {
            tvDate.text = data.travelDate
            mealReportRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            mealReportRecyclerView.adapter = MealReportAdapter(data.info!!,currency,currencyFormat,context)

            showHide.setOnClickListener {
                if (mealReportRecyclerView.visibility == View.GONE) {
                    mealReportRecyclerView.visible()
                    showHide.setImageResource(R.drawable.ic_blue_up_arrow)
                } else {
                    mealReportRecyclerView.gone()
                    showHide.setImageResource(R.drawable.ic_blue_down_arrow)
                }
            }
        }
    }
}