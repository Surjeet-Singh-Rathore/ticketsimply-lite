package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemAdapterClick
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterChildCheckInspectorBinding
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import gone


class CheckingInspectorAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemAdapterClick,
    private var list: ArrayList<Service>,
    private val isPending: Boolean
) :
    RecyclerView.Adapter<CheckingInspectorAdapter.ViewHolder>(), OnItemClickListener{
    private val viewPool = RecyclerView.RecycledViewPool()

    private var mOriginalValues // Original Values
            : ArrayList<Service?>? = null
    private var mDisplayedValues // Values to be displayed
            : ArrayList<Service?>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterChildCheckInspectorBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)
        holder.pnrTV.text = data.number.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.seatsTV.text = data.totalSeats.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.busInfoTV.text = (data.origin.toString() + " - " + data.destination.toString())?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.timeTV.text = data.arrivalTime.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable

        if(!isPending){
            holder.inspectTV.gone()
        }

        holder.inspectTV.setOnClickListener {
                onItemClickListener.onItemClick(isPending,holder.inspectTV,position)
        }




    }

    class ViewHolder(binding: AdapterChildCheckInspectorBinding) : RecyclerView.ViewHolder(binding.root) {
        val seatsTV = binding.seatsTV
        val inspectTV = binding.inspectTV
        val pnrTV = binding.pnrTV
        val busInfoTV = binding.busInfoTV
        val timeTV = binding.timeTV


    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    fun updateList(newList: ArrayList<Service>) {
        list = newList
        notifyDataSetChanged()
    }

    /*override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                mDisplayedValues = results.values as ArrayList<Service?> // has the filtered values
                notifyDataSetChanged() // notifies the data with new filtered values
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var constraint: CharSequence? = constraint
                val results =
                    FilterResults() // Holds the results of a filtering operation in values
                val FilteredArrList: ArrayList<Service> = ArrayList()
                if (mOriginalValues == null) {
                    mOriginalValues = ArrayList<Service?>(mDisplayedValues) // saves the original data in mOriginalValues
                }
                *//********
                 *
                 * If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 * else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 *//*
                if (constraint == null || constraint.length == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues!!.size
                    results.values = mOriginalValues
                } else {
                    constraint = constraint.toString().lowercase(Locale.getDefault())
                    for (i in 0 until mOriginalValues!!.size) {
                        val data: String? = mOriginalValues!![i]!!.name
                        if (data!!.lowercase(Locale.getDefault()).startsWith(constraint.toString())) {
                            FilteredArrList.add(mDisplayedValues!![i]!!)
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size
                    results.values = FilteredArrList
                }
                return results
            }
        }
    }*/
}