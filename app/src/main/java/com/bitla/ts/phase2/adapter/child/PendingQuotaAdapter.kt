package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildPhoneblockedPendingquotaItemBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.PassengerDetail
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone

class PendingQuotaAdapter(
    private val context: Context,
    private val items: MutableList<PassengerDetail>?,
    val privileges: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<PendingQuotaAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildPhoneblockedPendingquotaItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )


        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (items != null) {
            items.count()
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items?.get(position)
        holder.tvSeatNumber.text = item?.seatNo
        holder.tvName.text = item?.name
        holder.tvBookedBy.text = "${context.getString(R.string.blocked_by)}: ${item?.blockedBy}"

        var newRev = item?.collection.toString().replace(privileges?.currency ?:"₹", "")
        newRev = newRev.toDouble().convert(privileges?.currencyFormat ?: context.getString(R.string.indian_currency_format))

        holder.tvCollectionValue.text = "${privileges?.currency ?:"₹"}${newRev}"
        holder.tvTime.text = item?.dateTime
        holder.tvName.gone()
    }

    class ViewHolder(binding: ChildPhoneblockedPendingquotaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvSeatNumber = binding.tvSeatNumber
        val tvName = binding.tvName
        val tvBookedBy = binding.tvBlockedBy
        val tvCollectionValue = binding.tvCollectionValue
        val tvTime = binding.tvTime
    }
}