package com.bitla.restaurant_app.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.presentation.pojo.reports.ReportData
import com.bitla.restaurant_app.presentation.utils.gone
import com.bitla.restaurant_app.presentation.utils.visible

class MealReportDateAdapter(
    private var reportList: ArrayList<ReportData>,
    private val currency: String,
    private val currencyFormat: String,
) :
    RecyclerView.Adapter<MealReportDateAdapter.DateViewHolder>() {


    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val mealReportRecyclerView: RecyclerView = view.findViewById(R.id.meal_report_recyclerView)
        val showHide: ImageView = view.findViewById(R.id.showHide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_report_date_item, parent, false)
        return DateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val data = reportList[position]
        holder.apply {
            tvDate.text = data.travelDate
            mealReportRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            mealReportRecyclerView.adapter = MealReportAdapter(data.info!!,currency,currencyFormat)

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