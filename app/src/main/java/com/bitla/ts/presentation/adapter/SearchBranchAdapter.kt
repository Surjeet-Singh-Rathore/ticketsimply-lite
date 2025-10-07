package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSearchServiceBinding
import com.bitla.ts.domain.pojo.BranchModel.Branch

class SearchBranchAdapter(
    val context: Context,
    val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SearchBranchAdapter.ViewHolder>() {

    var totalSelectedServices = 0
    var branchListFiltered = mutableListOf<Branch>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSearchServiceBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = branchListFiltered[position]

        holder.tvBranchName.text = item.value
        holder.checkBoxBranch.isChecked = item.isChecked

        holder.checkBoxBranch.setOnClickListener {
            branchListFiltered[position].isChecked = holder.checkBoxBranch.isChecked.not()
            onItemClickListener.onClickOfItem(
                holder.checkBoxBranch.isChecked.toString(),
                item.id ?: 0
            )
        }
        holder.layout.setOnClickListener {
            holder.checkBoxBranch.performClick()
        }

    }

    override fun getItemCount(): Int {
        return branchListFiltered.size
    }

    fun addData(branchList: MutableList<Branch>) {
        branchListFiltered = branchList
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ChildSearchServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBoxBranch = binding.checkBoxService
        val layout = binding.layout
        val tvBranchName = binding.tvServiceName
    }
}