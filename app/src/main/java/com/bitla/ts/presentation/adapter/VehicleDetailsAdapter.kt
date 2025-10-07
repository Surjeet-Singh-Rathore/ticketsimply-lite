package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildVehicleDetailsBinding
import com.bitla.ts.domain.pojo.get_coach_documents.response.CoachDocumentsResponseItem
import gone
import visible


class VehicleDetailsAdapter(
    private val context: Context,
    private val mList: MutableList<CoachDocumentsResponseItem>,
    private val onViewClick: ((position: Int, imageURL: String) -> Unit),
    private val onShareClick: ((position: Int, imageURL: String,actionName : String) -> Unit),
    private val onDownloadClick: ((position: Int, imageURL: String) -> Unit),
) :
    RecyclerView.Adapter<VehicleDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildVehicleDetailsBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mList[position]
        if (item.imageUrl.isNullOrEmpty()) {
            holder.container.gone()
            holder.tvNotUpdated.visible()
        } else {

            holder.ivDownload.setOnClickListener {
                val popupMenu = PopupMenu(context, holder.ivDownload)
                popupMenu.getMenuInflater().inflate(R.menu.vehicle_document_menu, popupMenu.getMenu())
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                        when(menuItem.itemId){
                            R.id.downloadMI -> {
                                onDownloadClick.invoke(position, item.imageUrl)                            }
                            R.id.viewMI -> {
                                onViewClick.invoke(position, item.imageUrl)
                            }
                            R.id.shareMI -> {
                                onShareClick.invoke(position, item.imageUrl, item.alertTitle.toString())
                            }
                        }
                        return true
                    }
                })
                popupMenu.setForceShowIcon(true)
                popupMenu.show()
            }
        }
        holder.tvMain.text = mList[position].alertTitle
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(binding: ChildVehicleDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        val container = binding.containerCheck
        val tvNotUpdated = binding.tvNotUploaded
        val ivView = binding.ivView
        val ivDownload = binding.ivDownload
        val ivShare = binding.ivShare
        val tvMain = binding.tvMain
    }
}