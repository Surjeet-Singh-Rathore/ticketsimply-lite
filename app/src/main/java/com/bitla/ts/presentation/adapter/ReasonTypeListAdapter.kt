package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildReasonTypeAdapterBinding
import com.bitla.ts.domain.pojo.announcement_model.ReasonTypeSubItemModel
import com.bitla.ts.domain.pojo.announcement_model.ReasonTypeTitleAdapter
import com.bitla.ts.domain.pojo.available_routes.Result

class ReasonTypeListAdapter(
    private val context: Context,
//    private var searchList: ArrayList<ReasionTypeHeaderModel>,
    private val onItemClickListener: OnItemClickListener,


    ) :
    RecyclerView.Adapter<ReasonTypeListAdapter.ViewHolder>(), OnItemClickListener, Filterable {

    private var searchList: ArrayList<ReasonTypeSubItemModel> = ArrayList()
    var searchListFiltered: ArrayList<ReasonTypeSubItemModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildReasonTypeAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<ReasonTypeSubItemModel>) {
        searchList = list as ArrayList<ReasonTypeSubItemModel>
        searchListFiltered = searchList
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ChildReasonTypeAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvDateTime = binding.header
        val rvNestedReason = binding.rvstatusList


        fun bind(position: Int) {
//            val recyclerViewModel = searchList[position]
            tvDateTime.text = searchListFiltered[position].name
//            rvNestedReason = recyclerViewModel.subItemList


            setReasonTypeRecyclerView(
                rvNestedReason,
                searchListFiltered[position].subItemList
            )
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                searchListFiltered =
                    if (charString.isEmpty()) searchList else {
                        val filteredList = ArrayList<ReasonTypeSubItemModel>()
                        searchList
                            .filter {
                                (it.name.lowercase().contains(constraint.toString().lowercase())
                                        && (it.name.uppercase()
                                    .contains(constraint.toString().uppercase())))

                            }
                            .forEach { filteredList.add(it) }
                        filteredList

                    }
                return FilterResults().apply { values = searchListFiltered }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                searchListFiltered = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<ReasonTypeSubItemModel>
                notifyDataSetChanged()
            }
        }
    }


    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

        onItemClickListener.onClickOfItem(data, position)
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    fun setReasonTypeRecyclerView(recyclerView: RecyclerView, categries: ArrayList<String>) {

        val postAdapter = ReasonTypeTitleAdapter(context, categries, this)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = postAdapter
    }
}