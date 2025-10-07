package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildBookHistoryDateBinding

class BookingHistoryDateAdapter(
    private val context: Context?,
    private val onItemClickListener: Context?,
    private var bookingHistoryList: ArrayList<com.bitla.ts.domain.pojo.booking_history.response.Result>,

    ) :
    RecyclerView.Adapter<BookingHistoryDateAdapter.ViewHolder>() {

    //    private var tag: String = BusFilterAdapter::class.java.simpleName

    //    private lateinit var binding: ActivityBookingHistoryBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildBookHistoryDateBinding.inflate(LayoutInflater.from(context), parent, false)

//        val searchList = arrayListOf<Number>(1, 2, 3, 4)
//
//        layoutManager = LinearLayoutManager(context?.applicationContext, LinearLayoutManager.VERTICAL, false)
//        binding.rvBookingHistory.layoutManager = layoutManager
//        favouriteReportsAdapter =
//            BookingHistoryAdapter(
//                context?.applicationContext,
//                context?.applicationContext,
//                searchList
//            )
//        binding.rvBookingHistory.adapter = favouriteReportsAdapter

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookingHistoryList.size
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val bookingHistoryData:  com.bitla.ts.domain.pojo.booking_history.response.Result = bookingHistoryList[position]
//        holder.tvPnrNumber.text = bookingHistoryData.pnrNumber
//        holder.tvDescription.text = bookingHistoryData.description
//        holder.tvDescription.text = Html.fromHtml(bookingHistoryData.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
//        holder.tvEdit.tag = context.getString(R.string.edit)
//        holder.tvEdit.setOnClickListener {
//            onItemClickListener.onClick(holder.tvEdit, position)
//        }

        try {
            val bookingHistoryData: com.bitla.ts.domain.pojo.booking_history.response.Result =
                bookingHistoryList[position]

            holder.tvPnrNumber.text = bookingHistoryData.pnrNumber

            when {
                bookingHistoryData.description == null -> {
                    // return an empty spannable if the html is null
                    holder.tvDescription.text = ""
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                    // we are using this flag to give a consistent behaviour
                    holder.tvDescription.text =
                        Html.fromHtml(bookingHistoryData.description, Html.FROM_HTML_MODE_LEGACY)
                }
                else -> {
                    holder.tvDescription.text = Html.fromHtml(bookingHistoryData.description)
                }
            }
        } catch (e: Exception) {
        }
    }

    class ViewHolder(binding: ChildBookHistoryDateBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvPnrNumber = binding.bookingHistoryPnrNo
        val tvDescription = binding.bookingHistoryDescription
    }
}