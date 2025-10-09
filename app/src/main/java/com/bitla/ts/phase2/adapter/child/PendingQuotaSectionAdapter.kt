package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildPhoneblockedHeaderBinding
import com.bitla.ts.databinding.ChildQuotaBlockedHeaderBinding
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.PassengerDetail
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.Service
import com.bitla.ts.presentation.adapter.InterStationAdapter
import timber.log.Timber

class PendingQuotaSectionAdapter(
    private val context: Context,
    private val items: MutableList<Service>?,
    private var onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<PendingQuotaSectionAdapter.PhoneBlockedSectionViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhoneBlockedSectionViewHolder {
        val binding =
            ChildQuotaBlockedHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
        return PhoneBlockedSectionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (items != null) {
            Timber.d("testCount - ${items.count()}")
            return items.count()
        } else return 0
    }

    override fun onBindViewHolder(holder: PhoneBlockedSectionViewHolder, position: Int) {
        val name = items?.get(position)?.serviceNo
        val origin = items?.get(position)?.origin
        val destination = items?.get(position)?.destination
        val sections = items?.get(position)?.passengerDetails


//        holder.recyclerView.layoutManager = layoutManager
//        holder.recyclerView.adapter = adapter
//        holder.recyclerView.setRecycledViewPool(viewPool)
//        holder.recyclerView.setHasFixedSize(true)
//        holder.recyclerView.isNestedScrollingEnabled = false
//        holder.recyclerView.setItemViewCacheSize(10)

        holder.title.text = "$name - $origin - $destination"

        holder.cardViewQuotaBlock.setOnClickListener {
            onItemClickListener.onClickOfItem("", position)
        }
    }

    class PhoneBlockedSectionViewHolder(binding: ChildQuotaBlockedHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var title: TextView = binding.title
        var cardViewQuotaBlock: CardView = binding.cardViewQuotaBlock
        var recyclerView: RecyclerView = binding.recyclerViewSection
    }
}