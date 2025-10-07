package com.bitla.ts.presentation.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.ChildReservationChartBinding
import com.bitla.ts.databinding.ChildVanChartBinding
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.PickuVanService
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.view.activity.ServiceDetailsActivity
import com.bitla.ts.presentation.view.activity.SmsNotificationActivity
import com.bitla.ts.presentation.view.activity.reservationOption.announcement.AnnouncementActivity
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.ExtendedFair
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.UpdateRateCardActivity
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import gone
import timber.log.Timber
import toast
import visible
import java.lang.reflect.Method
import java.util.*

class VanChartAdapter(
    private val context: Context,
    private val setSchedule: ((schedule_id: Int?) -> Unit),
    private var searchList: ArrayList<PickuVanService>,
) :
    RecyclerView.Adapter<VanChartAdapter.ViewHolder>()
    {

    private var loginModelPref: LoginModel = LoginModel()
    private var countryList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildVanChartBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size

    }

    @SuppressLint("RtlHardcoded", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel:PickuVanService=
            searchList[position]

        holder.routeInfo.text ="${searchModel.departure_time} | ${searchModel.city}"
        holder.numberInfo.text = searchModel.pickup_van_no
        if (searchModel.coach_number == "") {
            holder.coach_name.gone()
        } else {
            holder.coach_name.text =searchModel.coach_number
        }
        holder.bookedSeats.text= "${context.getString(R.string.booked)}(${searchModel.booked_seats})"

        holder.btnViewReservationChart.setOnClickListener {

            setSchedule.invoke(searchModel.schedule_id)

            firebaseLogEvent(
                context,
                VIEW_RESERVATION_CHART,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                VIEW_RESERVATION_CHART,
                "ViewReservation Chart Clicks"
            )
        }

        holder.cardlayout.setCardBackgroundColor(context.resources.getColor(R.color.white))
    }


    inner class ViewHolder(binding: ChildVanChartBinding) :
        RecyclerView.ViewHolder(binding.root) {


        val btnViewReservationChart = binding.btnViewReservationChart


        val routeInfo = binding.routeInfo

        val numberInfo = binding.numberinfo


        val coach_name = binding.coachName
        val bookedSeats = binding.totalBooked

        val cardlayout = binding.cardLayout

    }

}