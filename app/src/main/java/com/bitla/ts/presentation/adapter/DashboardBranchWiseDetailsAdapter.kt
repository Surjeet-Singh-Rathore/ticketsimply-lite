package com.bitla.ts.presentation.adapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterDashboardBranchWiseDetailsBinding
import com.bitla.ts.domain.pojo.dashboard_branchwise_revenue_popup.Detail

class DashboardBranchWiseDetailsAdapter (
    private val context: Context?,
    private val branchWiseRevenueDetailsList: List<Detail?>,

) :
    RecyclerView.Adapter<DashboardBranchWiseDetailsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterDashboardBranchWiseDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return branchWiseRevenueDetailsList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val branchWiseRevenueDetailItem = branchWiseRevenueDetailsList[position]

        holder.userName.text = branchWiseRevenueDetailItem?.user
        holder.seatsCount.text = branchWiseRevenueDetailItem?.seats
        holder.tvRevenue.text = branchWiseRevenueDetailItem?.revenue
    }

    class ViewHolder(binding: AdapterDashboardBranchWiseDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val userName = binding.titleTV
        val seatsCount = binding.title1TV
        val tvRevenue = binding.title2TV



    }
}