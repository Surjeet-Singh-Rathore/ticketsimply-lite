package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildCoachListsAdapterBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel

class CoachtypeListsAdapter(
    private val context: Context,
    private var searchList: List<SearchModel>
) :
    RecyclerView.Adapter<CoachtypeListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCoachListsAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val searchModel:SearchModel = searchList[position]

    }

    class ViewHolder(binding: ChildCoachListsAdapterBinding) : RecyclerView.ViewHolder(binding.root)
}