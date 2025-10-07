package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ItemNavMenuBinding
import com.bitla.ts.domain.pojo.dashboard_model.NavMenuModel


class NavigationMenuAdapter(
    private val context: Context,
    private var menuList: List<NavMenuModel>,
    private val onItemClick: ((title: String) -> Unit)
) :
    RecyclerView.Adapter<NavigationMenuAdapter.ViewHolder>() {
    private var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNavMenuBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: NavMenuModel = menuList[position]
        holder.title.text = menu.title
        holder.imageview.setImageResource(menu.image)

        holder.container.setOnClickListener {
            onItemClick.invoke(menu.title)
        }

        holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorDimShadow6))
        TextViewCompat.setTextAppearance(holder.title, R.style.msg_context)
        DrawableCompat.setTint(
            holder.imageview.drawable,
            ContextCompat.getColor(context, R.color.colorDimShadow6)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun menuColorChange(position: Int) {
        this.selectedPosition = position
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ItemNavMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.menuTitle
        val imageview = binding.menuImage
        val container = binding.menuContainer
    }
}