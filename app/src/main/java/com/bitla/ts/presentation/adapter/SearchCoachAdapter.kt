package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildSearchSelectionBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.domain.pojo.get_coach_details.response.CoachDetailsResponseItem
import timber.log.Timber

class SearchCoachAdapter(
    val context: Context,
    var coachDetailsList: MutableList<CoachDetailsResponseItem>,
    val onCoachSelected: ((position: Int, item: CoachDetailsResponseItem) -> Unit)
) : RecyclerView.Adapter<SearchCoachAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSearchSelectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = coachDetailsList[position]

        holder.tvSearch.text = "${item.coachNumber}"

        holder.layoutSearchSelection.setOnClickListener {
            onCoachSelected.invoke(position, item)
        }


    }

    override fun getItemCount(): Int {
        return coachDetailsList.size
    }

    fun filterList(filteredList: MutableList<CoachDetailsResponseItem>) {
        this.coachDetailsList = filteredList
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ChildSearchSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvSearch = binding.tvSearch
        val layoutSearchSelection = binding.layoutSearchSelection
    }
}