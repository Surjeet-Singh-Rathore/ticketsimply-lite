package com.bitla.ts.presentation.view.activity.addRateCard.fetchShowRateCard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnMenuItemClickListener
import com.bitla.ts.databinding.ChildRateCardMainBinding
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response.RouteWiseRateCardDetail
import com.bitla.ts.utils.common.getDateYMD
import com.bitla.ts.utils.common.getTodayDate
import java.lang.reflect.Method

class FetchShowRateCardAdapter(

    private val context: Context,
    private var routeWiseRateCardDetailList: MutableList<RouteWiseRateCardDetail>,
    private val onMenuItemClickListener: OnMenuItemClickListener,

    ) :
    RecyclerView.Adapter<FetchShowRateCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildRateCardMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return routeWiseRateCardDetailList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val fetchShowRateCardResponse: RouteWiseRateCardDetail = routeWiseRateCardDetailList[position]
        holder.apply {
            tvStartDateValue.text = fetchShowRateCardResponse.startDate
            tvEndDateValue.text = fetchShowRateCardResponse.endDate
            tvRateCardNameValue.text = fetchShowRateCardResponse.name
            tvCoachTypeValue.text = fetchShowRateCardResponse.coachType

            imgMore.setOnClickListener {
                setPopup(context, holder.imgMore, position)
            }

            if (routeWiseRateCardDetailList[position].endDate < getDateYMD(getTodayDate())) {
                viewContainer.setCardBackgroundColor(ContextCompat.getColor(context,R.color.light_color_rate_card))
            } else {
                viewContainer.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))

            }
        }

    }

    class ViewHolder(binding: ChildRateCardMainBinding) : RecyclerView.ViewHolder(binding.root) {
        val imgMore = binding.imgMore
        val tvStartDateValue = binding.tvStartDateValue
        val tvEndDateValue = binding.tvEndDateValue
        val tvRateCardNameValue = binding.tvRateCardNameValue
        val tvCoachTypeValue = binding.tvCoachTypeValue
        val viewContainer = binding.cardView
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setPopup(context: Context, view: View, position: Int) {
        val popup = PopupMenu(context, view)

        popup.inflate(R.menu.rate_card_options_menu)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

//        Timber.d("currentDate =: ${getDateYMD(getTodayDate())} --- RateCardDate  =:  ${routeWiseRateCardDetailList[position].endDate}")
//        Timber.d("currentDate-Check= ${routeWiseRateCardDetailList[position].endDate >= getDateYMD(getTodayDate())}")

        val itemEdiFare = popup.menu.getItem(2)
        itemEdiFare.isVisible = false


        val itemDelete = popup.menu.getItem(3)
        itemDelete.isVisible = routeWiseRateCardDetailList[position].isDelete



        if ((routeWiseRateCardDetailList[position].endDate == getDateYMD(getTodayDate())
                    && routeWiseRateCardDetailList[position].startDate == getDateYMD(getTodayDate()))
            || (routeWiseRateCardDetailList[position].endDate > getDateYMD(getTodayDate())
                    && routeWiseRateCardDetailList[position].startDate > getDateYMD(getTodayDate()))
        ) {
            itemEdiFare.isVisible = true
        }

       /* if (routeWiseRateCardDetailList[position].endDate > getDateYMD(getTodayDate())
            && routeWiseRateCardDetailList[position].startDate != getDateYMD(getTodayDate())
        ) {
            itemDelete.isVisible = true
        }*/

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.item_view_rateCard -> {
                    onMenuItemClickListener.onMenuItemClick(
                        menuPosition = 0,
                        itemPosition = position,
                        label = routeWiseRateCardDetailList[position].name
                    )
                }

                R.id.item_show_rateCard -> {
                    onMenuItemClickListener.onMenuItemClick(
                        menuPosition = 1,
                        itemPosition = position,
                        label = routeWiseRateCardDetailList[position].name
                    )
                }

                R.id.item_edit_rateCard -> {
                    onMenuItemClickListener.onMenuItemClick(
                        menuPosition = 2,
                        itemPosition = position,
                        label = routeWiseRateCardDetailList[position].name
                    )
                }

                R.id.item_delete_rateCard -> {
                    onMenuItemClickListener.onMenuItemClick(
                        menuPosition = 3,
                        itemPosition = position,
                        label = routeWiseRateCardDetailList[position].name
                    )
                }
            }
            true
        }

        // show icons on popup menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        } else {
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popup]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        popup.show()

    }
}