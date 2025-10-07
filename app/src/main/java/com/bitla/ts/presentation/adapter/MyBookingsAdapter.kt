package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnPnrListener
import com.bitla.ts.databinding.ChildMyBookingsBinding
import com.bitla.ts.domain.pojo.my_bookings.response.Data
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.getDateMMM
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import timber.log.Timber
import visible
import java.lang.reflect.Method

class MyBookingsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onPnrListener: OnPnrListener,
    private var dataList: MutableList<Data>,
    private var privilegeResponseModel: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<MyBookingsAdapter.ViewHolder>() {
    private var tag: String = MyBookingsAdapter::class.java.simpleName
    private var currency: String? = null
    private var currencyFormat: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildMyBookingsBinding.inflate(LayoutInflater.from(context), parent, false)
        getPref()
        return ViewHolder(binding)
    }

    private fun getPref() {
        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel?.currency
                ?: context.getString(R.string.rupess_symble)
            currencyFormat =
                getCurrencyFormat(context, privilegeResponseModel?.currencyFormat)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataList[position]

        val serNo = data.serviceName
        val route = data.route
        var dep = data
        val seats = data.noOfSeats
        if (data.bookedOn != null) {
            try {
                val booked = getDateMMM(data.bookedOn.substring(0, 10))
                val bookedOnTime = data.bookedOn.substringAfter(" ")
                //val totalBooked = "$bookedOnTime | $seats Passengers| Booked on $booked"
                val totalBooked =
                    "$seats ${context.getString(R.string.passengers)}| ${context.getString(R.string.booked_on)} ${data.bookedOn}"

                holder.tvPassengers.text = totalBooked
            } catch (e: Exception) {
                Timber.d("ExceptionMsg ${e.message}")
            }
        }
        if (data.totalFare != null) {
            //val fare = ((data.totalFare * 100.00).roundToInt() / 100.00)
            val fare = "${data.totalFare.convert(currencyFormat)}"
            val fareWithCurrency = "$currency $fare"
            holder.tvAmount.text = fareWithCurrency
        }
        holder.tvPnr.text = data.pnrNumber
        val serNoAndRoute = "$serNo ($route)"
        holder.tvBusInfo.text = serNoAndRoute

        if (data.isUpdated) {
            holder.status.text = context.getString(R.string.updatedUpperCase)
            holder.status.visible()
        } else
            holder.status.gone()

        holder.viewTicket.setOnClickListener {
            onPnrListener.onPnrSelection(
                context.getString(R.string.view_ticket),
                data.pnrNumber
            )
        }

        if (!data.isUpdatable && !data.isCancellable) {
            holder.imgMore.gone()
        }

//        context.toast("cancel ${data.isCancellable}")
//        context.toast("update ${data.isUpdatable}")

        holder.imgMore.setOnClickListener {
            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.mybooking_options)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.gravity = Gravity.RIGHT
            }
            if (data.isUpdatable) {
                val itemPosition = popup.menu.getItem(0)
                itemPosition.isVisible = true
            } else {
                val itemPosition = popup.menu.getItem(0)
                itemPosition.isVisible = false
            }

            if (data.isCancellable) {
                val itemPosition = popup.menu.getItem(1)
                itemPosition.isVisible = true
            } else {
                val itemPosition = popup.menu.getItem(1)
                itemPosition.isVisible = false

            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.editPassengerDetails -> {
                        onPnrListener.onPnrSelection(
                            context.getString(R.string.edit_passenger_details),
                            data.pnrNumber
                        )
                    }

                    R.id.cancelTicket -> {
                        onPnrListener.onPnrSelection(
                            context.getString(R.string.cancel_ticket),
                            data.pnrNumber
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

    class ViewHolder(binding: ChildMyBookingsBinding) : RecyclerView.ViewHolder(binding.root) {
        var tvPnr = binding.tvPnr
        var tvAmount = binding.tvAmount
        var tvPassengers = binding.tvPassengers
        var tvBusInfo = binding.tvBusInfo
        val imgMore = binding.imgMore
        val status = binding.tvStatus
        val viewTicket = binding.viewTicket
    }
}