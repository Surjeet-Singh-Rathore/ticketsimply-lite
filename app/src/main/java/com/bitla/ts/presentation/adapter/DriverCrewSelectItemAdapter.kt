package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCrewSelectedItemBinding
import com.bitla.ts.domain.pojo.employees_details.response.Employee
import timber.log.Timber
import java.util.*

class DriverCrewSelectItemAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<DriverCrewSelectItemAdapter.ViewHolder>(), Filterable {

    private var dataSet: ArrayList<Employee> = ArrayList()
    private var filterList: ArrayList<Employee> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCrewSelectedItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val searchEmployeeModel: Employee = filterList[position]

        if (searchEmployeeModel.mobileNumber != null && searchEmployeeModel.mobileNumber.isNotEmpty()) {
            holder.name.text = "${searchEmployeeModel.name} - ${searchEmployeeModel.mobileNumber}"
        } else {
            holder.name.text = searchEmployeeModel.name
        }

        holder.mainLayout.setOnClickListener {
            onItemClickListener.onClickOfItem(holder.name.text.toString(), searchEmployeeModel.id)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<Employee>) {
        dataSet = list as ArrayList<Employee>
        filterList = dataSet
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ChildCrewSelectedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvName
        val mobile = binding.tvMobile
        val mainLayout = binding.mainLayout
    }

    override fun getFilter(): Filter? {
        return searchedFilter
    }

    private val searchedFilter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: ArrayList<Employee> = ArrayList()

            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(dataSet)
            } else {

                for (i in 0..dataSet.size.minus(1)) {
                    Timber.d("itemSet-item- ${dataSet[i]}")
                    try {

                        if (dataSet[i].mobileNumber.isNotEmpty() && dataSet[i].mobileNumber.contains(
                                constraint
                            )
                        ) {
                            Timber.d("itemSet-item- ${dataSet[i]}")
                            filteredList.add(dataSet[i])
                        } else if (dataSet[i].name.lowercase()
                                .contains(constraint.toString().toLowerCase())
                        ) {
                            Timber.d("itemSet-item- ${dataSet[i]}")
                            filteredList.add(dataSet[i])
                        }
                    } catch (e: Exception) {
                        Timber.d("itemSet123-${e.stackTrace} ")
                    }
                }

            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {

            filterList = if (results.values == null) {
                ArrayList()
            } else {
                results.values as ArrayList<Employee>
            }
            notifyDataSetChanged()
            Timber.d("itemSet-filterList- ${results.values} ")
        }
    }
}