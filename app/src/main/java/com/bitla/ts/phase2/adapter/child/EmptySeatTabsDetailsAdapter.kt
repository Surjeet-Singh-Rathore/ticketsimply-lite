package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildEmptySeatDetailsBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class EmptySeatTabsDetailsAdapter(
    private val context: Context,
    private var occupancyAllTabsDetailsModel: MutableList<OccupancyAllTabsDetailsModel>,
    private val showCurrencyFormat: Boolean? = false,
    val privilegeResponse: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<EmptySeatTabsDetailsAdapter.ViewHolder>() {

    private var currency = privilegeResponse?.currency ?: ""
    private var currencyFormat = privilegeResponse?.currencyFormat ?: context.getString(R.string.indian_currency_format)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildEmptySeatDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return occupancyAllTabsDetailsModel.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val occupancyAllTabsDetailsData: OccupancyAllTabsDetailsModel =
            occupancyAllTabsDetailsModel[position]

        holder.tvTitle.text = occupancyAllTabsDetailsData.title

        var value = occupancyAllTabsDetailsData.value ?: "0.0"

        if (showCurrencyFormat == true) {
            var newValue = value.replace(currency, "")

            holder.tvValue.text = currency + newValue.toDouble().convert(
                currencyFormat
            )
        } else {
            holder.tvValue.text = value
        }


        //holder.tvValue.text = occupancyAllTabsDetailsData.value
        holder.tvEmptySeats.text = "${occupancyAllTabsDetailsData.emptySeatsCount}"
    }

    class ViewHolder(binding: ChildEmptySeatDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvValue = binding.tvValue
        val tvEmptySeats = binding.tvEmptySeats
    }
}