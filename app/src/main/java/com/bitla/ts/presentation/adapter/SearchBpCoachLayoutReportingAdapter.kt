package com.bitla.ts.presentation.adapter

import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.reservation_stages.response.StageItem
import java.util.ArrayList


class SearchBpCoachLayoutReportingAdapter(
    private val context: Context,
    private var stageDetails: ArrayList<StageItem>,
    var onItemClick: ((item: StageItem) -> Unit)
) : RecyclerView.Adapter<SearchBpCoachLayoutReportingAdapter.ViewHolder>(), Filterable {
    var stageDetailsList: ArrayList<StageItem> = stageDetails

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterSimpleListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stageDetailsList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = stageDetailsList[position]
        holder.title.text = context.getString(
            R.string.stage_details_text, data.stageTime, data.cityName, data.stageName
        )

        holder.title.setOnClickListener {
            onItemClick.invoke(stageDetailsList[position])
        }
    }

    class ViewHolder(binding: AdapterSimpleListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleTV
        val bottomV = binding.bottomV
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                stageDetailsList =
                    if (charString.isEmpty()) stageDetails else {
                        val filteredList = ArrayList<StageItem>()
                        stageDetails
                            .filter {
                                (it.stageName?.lowercase()?.contains(constraint.toString().lowercase()) == true
                                        || it.cityName?.lowercase()?.contains(constraint.toString().lowercase()) == true
                                        || it.stageTime?.lowercase()?.contains(constraint.toString().lowercase()) == true)

                            }
                            .forEach { filteredList.add(it) }
                        filteredList

                    }

                return FilterResults().apply { values = stageDetailsList }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                stageDetailsList = if (results?.values == null)
                    ArrayList<StageItem>()
                else
                    results.values as ArrayList<StageItem>
                notifyDataSetChanged()
            }
        }
    }
}