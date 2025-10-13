package com.bitla.ts.phase2.adapter.child

import android.annotation.*
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildOccupancyDetailsitemBinding
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import timber.log.*
import visible

class OccupancyAllTabsDetailsAdapter(
    private val context: Context,
    val privilegeResponseModel: PrivilegeResponseModel?,
    private var serviceWiseOccupancyList: MutableList<OccupancyAllTabsDetailsModel>,
    private val showGrossNetAmount: Boolean? = false,
    private val showCurrencyFormat: Boolean? = false,
    private val onItemClick:((position: Int)->Unit)? = null
) :
    RecyclerView.Adapter<OccupancyAllTabsDetailsAdapter.ViewHolder>(), Filterable  {

    private var currency = privilegeResponseModel?.currency ?: ""
    private var currencyFormat = privilegeResponseModel?.currencyFormat ?: context.getString(R.string.indian_currency_format)
    var serviceWiseOccupancyFilteredList: MutableList<OccupancyAllTabsDetailsModel> = serviceWiseOccupancyList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildOccupancyDetailsitemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return serviceWiseOccupancyFilteredList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val occupancyAllTabsDetailsData: OccupancyAllTabsDetailsModel = serviceWiseOccupancyFilteredList[position]

        if (showGrossNetAmount == true) {
            holder.cardView.gone()
            holder.cardView2.visible()
            holder.tvTitle2.text = occupancyAllTabsDetailsData.title

            val grossRev = occupancyAllTabsDetailsData.grossRevenue ?: "0.0"
            val netRev = occupancyAllTabsDetailsData.netRevenue ?: "0.0"

            if (showCurrencyFormat == true) {
                val newNetrev = netRev.replace(currency, "")
                val newGrossRev = grossRev.replace(currency, "")

                holder.tvNetAmount.text = currency + newNetrev.toDouble().convert(
                    currencyFormat
                )
                holder.tvGrossAmount.text = currency + newGrossRev.toDouble().convert(
                    currencyFormat
                )
            } else {
                holder.tvNetAmount.text = netRev
                holder.tvGrossAmount.text = grossRev
            }

        } else {
            holder.cardView2.gone()
            holder.cardView.visible()

            val value = occupancyAllTabsDetailsData.value ?: "0.0"

            if (showCurrencyFormat == true) {
                val newValue = value.replace(currency, "")

                holder.tvValue.text = currency + newValue.toDouble().convert(
                    currencyFormat
                )
            } else {
                holder.tvValue.text = value
            }

            holder.tvTitle.text = occupancyAllTabsDetailsData.title
            holder.tvTitle.setOnClickListener{
                onItemClick?.invoke(position)
            }
        }

    }

    class ViewHolder(binding: ChildOccupancyDetailsitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvValue = binding.tvValue
        val cardView = binding.cardView
        val cardView2 = binding.cardView2
        val tvTitle2 = binding.tvTitle2
        val tvGrossAmount = binding.tvGrossAmount
        val tvNetAmount = binding.tvNetAmount

    }
    
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                
                serviceWiseOccupancyFilteredList =
                    
                    if (charString.isEmpty()) serviceWiseOccupancyList else {
                        val filteredList = mutableListOf<OccupancyAllTabsDetailsModel>()
                        serviceWiseOccupancyList
                            .filter {
                                (it.title?.lowercase()?.contains(constraint.toString().lowercase()) == true)
                                
                            }
                            .forEach { filteredList.add(it) }
                        filteredList
                    }
                
                return FilterResults().apply { values = serviceWiseOccupancyFilteredList }
            }
            
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                
                serviceWiseOccupancyFilteredList = if (results?.values == null)
                    mutableListOf()
                else
                    results.values as MutableList<OccupancyAllTabsDetailsModel>
                notifyDataSetChanged()
            }
        }
    }
}