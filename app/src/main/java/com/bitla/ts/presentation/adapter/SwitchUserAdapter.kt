package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildSwitchUserBinding
import com.bitla.ts.domain.pojo.user.User
import com.bumptech.glide.Glide

class SwitchUserAdapter(
    private val context: Context,
    private var userList: List<User>,
    private val onItemClick: ((user: User) -> Unit)
) : RecyclerView.Adapter<SwitchUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSwitchUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        holder.tvOperatorName.text = item.travelsName
        holder.tvUsername.text = item.username
        holder.tvRole.text = item.role

        holder.root.setOnClickListener {
            onItemClick.invoke(item)
        }

        if (item.logoUrl.isEmpty()) {
            Glide.with(context).clear(holder.ivOperatorLogo)
            //holder.ivOperatorLogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ts_logo))

        } else {
            Glide.with(context)
                .load(item.logoUrl)
                .fitCenter()

                .circleCrop()
                .into(holder.ivOperatorLogo)

        }
    }

    class ViewHolder(binding: ChildSwitchUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val ivOperatorLogo = binding.ivOperatorLogo
        val tvOperatorName = binding.tvOperatorName
        val tvUsername = binding.tvUsername
        val tvRole = binding.tvRole
    }
}