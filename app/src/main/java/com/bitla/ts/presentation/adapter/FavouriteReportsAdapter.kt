package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildFavouriteReportBinding
import com.bitla.ts.domain.pojo.starred_reports.Report
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getDateDMY
import com.bitla.ts.utils.common.getDateDMYfromAny
import com.bitla.ts.utils.constants.DOWNLOAD_REPORT
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible

class FavouriteReportsAdapter(
    private val context: Context?,
    private var reportList: List<Report>,
    private var starredReport: Boolean,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<FavouriteReportsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildFavouriteReportBinding.inflate(LayoutInflater.from(context), parent, false)
        if (starredReport) {
            binding.starredReport.visible()
        } else {
            binding.starredReport.gone()
        }
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report: Report = reportList[position]


        holder.name.text = report.report_name

        val travelDate = PreferenceUtils.getTravelDate()

        if (report.rep_from_date?.isNotEmpty() == true && report.rep_to_date?.isNotEmpty() == true) {
            try {
                holder.dateRange.text =
                    "${context?.getString(R.string.from)} ${getDateDMY(report.rep_from_date)} ${
                        context?.getString(R.string.to)
                    } ${getDateDMY(report.rep_to_date.toString())}"
            } catch (e: Exception) {
                holder.dateRange.text =
                    "${context?.getString(R.string.from)} ${getDateDMYfromAny(report.rep_from_date.toString())} ${
                        context?.getString(R.string.to)
                    } ${getDateDMYfromAny(report.rep_to_date.toString())}"
            }
        } else {
            holder.dateRange.text =
                "${context?.getString(R.string.from)} $travelDate ${context?.getString(R.string.to)} $travelDate"
        }

        holder.srcDst.text = report.service_info

        holder.download.setOnClickListener {
            onItemClickListener.onClickOfItem(report.pdf_url, position)

            firebaseLogEvent(
                context!!,
                DOWNLOAD_REPORT,
                PreferenceUtils.getLogin().userName,
                PreferenceUtils.getLogin().travels_name,
                PreferenceUtils.getLogin().role,
                DOWNLOAD_REPORT,
                report.report_name
            )
        }
    }

    class ViewHolder(binding: ChildFavouriteReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.reportName
        val dateRange = binding.dateRange
        val srcDst = binding.serviceInfo
        val download = binding.downloadBtn
    }
}