package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildUsersLayoutBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel


class UsersAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var userList: List<LoginModel>
) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildUsersLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loginModel: LoginModel = userList[position]

        if (loginModel.name.isNotEmpty())
            holder.tvUserTitle.text = loginModel.name.substring(0, 2).toUpperCase()

/*        if (position == 0)
            holder.imageView.setImageResource(R.drawable.circle_shape_stroke)
        else
            holder.imageView.setImageResource(R.drawable.circle_shape)*/

        when {
            position == 0 ->
                holder.imageView.setImageResource(R.drawable.circle_shape_stroke_green)
            position == 1 ->
                holder.imageView.setImageResource(R.drawable.circle_shape_stroke_orange)
            position == 2 ->
                holder.imageView.setImageResource(R.drawable.circle_shape_stroke_purple)
            position == 3 ->
                holder.imageView.setImageResource(R.drawable.circle_shape_stroke_pink)
            position == 4 ->
                holder.imageView.setImageResource(R.drawable.circle_shape_stroke_red)
            else ->
                holder.imageView.setImageResource(R.drawable.circle_shape_stroke_yellow)
        }


        holder.tvUserTitle.setOnClickListener {
            onItemClickListener.onClick(holder.tvUserTitle, position)
        }
    }


    class ViewHolder(binding: ChildUsersLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvUserTitle = binding.tvUserTitle
        val imageView = binding.imageView
    }
}