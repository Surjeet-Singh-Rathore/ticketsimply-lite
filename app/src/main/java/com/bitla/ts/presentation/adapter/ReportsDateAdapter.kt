package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildReportDateBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.starred_reports.StarredReport
import com.bitla.ts.utils.common.getDateDMY

class ReportsDateAdapter(
    private val context: Context?,
    private var reportList: List<StarredReport>,
    private var starredReport: Boolean,
    private val onItemClickListener: OnItemClickListener

) :
    RecyclerView.Adapter<ReportsDateAdapter.ViewHolder>(), OnItemClickListener {

    //    private var tag: String = BusFilterAdapter::class.java.simpleName
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var favouriteReportsAdapter: FavouriteReportsAdapter


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildReportDateBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report: StarredReport = reportList[position] as StarredReport
        val searchList = report.reports


        holder.date.text =
            "${context!!.resources.getString(R.string.generated_on)} ${getDateDMY(report.date)}"
        layoutManager =
            LinearLayoutManager(context.applicationContext, LinearLayoutManager.VERTICAL, false)
        holder.reportList.layoutManager = layoutManager
        favouriteReportsAdapter =
            FavouriteReportsAdapter(
                context,
                searchList,
                starredReport,
                onItemClickListener
            )
        holder.reportList.adapter = favouriteReportsAdapter

    }

    class ViewHolder(binding: ChildReportDateBinding) : RecyclerView.ViewHolder(binding.root) {
        val reportList = binding.rvFavoutriteReports
        val date = binding.dateView
    }

    override fun onClickOfNavMenu(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onClick(view: View, position: Int) {
        TODO("Not yet implemented")

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {
        onItemClickListener.onClickOfItem(data, position)
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
        TODO("Not yet implemented")
    }
}