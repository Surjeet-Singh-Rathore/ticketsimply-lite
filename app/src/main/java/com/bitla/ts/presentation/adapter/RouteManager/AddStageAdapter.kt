package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.data.DEFAULT_HOURS
import com.bitla.ts.data.DEFAULT_MINUTES
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterSearchBpdpBinding
import com.bitla.ts.databinding.AdapterStageDetailsBinding
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.domain.pojo.stage_for_city.StageListData
import com.bitla.ts.domain.pojo.update_route.StageDetailsData
import com.bitla.ts.presentation.adapter.SourceDestinatinAdapter
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.common.openHoursMinsPickerDialog
import com.bitla.ts.utils.constants.SOURCE
import gone
import toast
import visible


class AddStageAdapter(
    private val context: Context,
    private var list: ArrayList<StageDetailsData>,
    var viewModel: RouteManagerViewModel<Any?>,
    var activity: Activity,
    var outerPos: Int,
    var dialogAnyClickListener: DialogAnyClickListener,
    var isBoarding: Boolean,
    var showFullData: Boolean) :
    RecyclerView.Adapter<AddStageAdapter.ViewHolder>(), DialogButtonAnyDataListener {

    private var coachTypeAdapter: SourceDestinatinAdapter? = null
    private var stagePopupWindow: PopupWindow? = null
    private  var stageNamee = ""
    private  var stageId = ""
    private var currentPos = 0
    private var showCompleteData = showFullData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterStageDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setData(holder,position)

        currentPos = holder.adapterPosition

        /*if(stageNamee.isNotBlank()){
            holder.stageName.text = stageNamee
            holder.stageDropDownET.setText(stageNamee)
        }*/

        holder.viewBinding.latitudeEV.setOnClickListener {

        }



        holder.dropDownButton.setOnClickListener {
            currentPos = holder.adapterPosition
            if (holder.mainGroup.isVisible) {
                holder.mainGroup.gone()
                holder.deleteButton.visible()
            } else {
                holder.mainGroup.visible()
                holder.deleteButton.gone()


            }
        }


        if(showCompleteData){
            if(position == list.size-1){
                holder.mainGroup.visible()
                holder.deleteButton.gone()
            }
        }else{
            holder.mainGroup.gone()
            holder.deleteButton.visible()

        }

        holder.viewBinding.stageDropDownTV.setOnClickListener {
            currentPos = holder.adapterPosition
            showStageListDropDown(holder.viewBinding.stageDropDownTV)
        }


        holder.deleteButton.setOnClickListener {
            if(list[holder.adapterPosition].id.isBlank() || list[holder.adapterPosition].id == "0"){
                val obj = StageListData()
                obj.id = list[holder.adapterPosition].defaultStageId
                obj.name = list[holder.adapterPosition].name
                if(obj.name.isNotBlank()){
                    viewModel.stageList.value?.add(0,obj)
                }
                list.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, list.size)
                dialogAnyClickListener.onAnyClickListener(2,"notify",position)
            }else{
                dialogAnyClickListener.onAnyClickListenerWithExtraParam(2,"delete_existing_stage",list,position,outerPos)

            }

        }

        holder.viewBinding.departureHoursET.setOnClickListener {
            openHoursMinsPickerDialog(activity = activity,context, DEFAULT_HOURS,holder.viewBinding.departureHoursET)
        }
        holder.viewBinding.departureMinutesET.setOnClickListener {
            openHoursMinsPickerDialog(activity = activity,context, DEFAULT_MINUTES,holder.viewBinding.departureMinutesET)
        }
        holder.viewBinding.journeyDayTV.setOnClickListener {
            openHoursMinsPickerDialog(activity = activity,context,5,holder.viewBinding.journeyDayTV,true)
        }


        holder.viewBinding.saveChangesTV.setOnClickListener {
            if(isValidate(holder.viewBinding)){
                if(stageId.isBlank()){
                    stageId = list[holder.adapterPosition].defaultStageId
                }
                holder.deleteButton.visible()
                val obj = StageDetailsData()
                obj.name = holder.viewBinding.stageNameTV.text.toString()
                if(viewModel.isEdit.value == true){
                    if(list[holder.adapterPosition].id == ""){
                        obj.id = "0"
                    }else{
                        obj.id = list[position].id

                    }
                }else{
                    obj.id = "0"
                }
                holder.viewBinding.apply {
                    obj.defaultStageId = stageId
                    obj.departureHour= departureHoursET.text.toString()
                    obj.departureMinute = departureMinutesET.text.toString()
                    obj.sendSms = sendSmsCB.isChecked
                    obj.journeyDay = journeyDayTV.text.toString()
                    obj.isPickup = isPickupCB.isChecked
                    obj.isEticketing = isEticketingCB.isChecked
                    obj.isApiBooking = isApiCB.isChecked
                    obj.person = personEV.text.toString()
                    obj.contactNo = contactNumberEV.text.toString()
                    obj.address1 = addressLine1EV.text.toString()
                    obj.address2 = addressLine2EV.text.toString()
                    obj.landmark = landmarkEV.text.toString()
                    obj.locationUrl = locationUrlEV.text.toString()
                    obj.lat = latitudeEV.text.toString()
                    obj.long = longitudeEV.text.toString()
                    obj.departure = departureHoursET.text.toString() + ":" + departureMinutesET.text.toString()
                }

                if(isBoarding){
                    viewModel.boardingPointList.value?.let { boardingList ->
                        boardingList[outerPos].stageList[holder.adapterPosition] = obj
                    }
                }else{
                    viewModel.droppingPointList.value?.let { boardingList ->
                        boardingList[outerPos].stageList[holder.adapterPosition] = obj
                    }
                }
                viewModel.stageList.value?.removeIf { data ->
                    data.id == stageId

                }
                context.toast(context.getString(R.string.details_saved))
                holder.mainGroup.gone()
            }






        }




    }

    private fun setData(holder: ViewHolder, position: Int) {


        val data = list[holder.adapterPosition]
        holder.viewBinding.apply {
            stageDropDownTV.setText(data.name)
            stageNameTV.text = data.name
            departureHoursET.setText(data.departureHour)
            departureMinutesET.setText(data.departureMinute)
            departureMinutesET.setText(data.departureMinute)
            sendSmsCB.isChecked = data.sendSms
            journeyDayTV.setText(data.journeyDay)
            isPickupCB.isChecked = data.isPickup
            isEticketingCB.isChecked = data.isEticketing
            isApiCB.isChecked = data.isApiBooking
            personEV.setText(data.person)
            contactNumberEV.setText(data.contactNo)
            addressLine1EV.setText(data.addressLine1)
            addressLine2EV.setText(data.addressLine2)
            landmarkEV.setText(data.landmark)
            locationUrlEV.setText(data.locationUrl)
            latitudeEV.setText(data.lat)
            longitudeEV.setText(data.long)
        }


        if(position == 0 && holder.viewBinding.stageNameTV.text.isNotEmpty() && data.departureHour.isBlank() && data.departureMinute.isBlank()) {
            if (isBoarding) {
                try {
                    if(outerPos <= (viewModel.viaCitiesList.value?.size?:0) -1){
                        holder.viewBinding.departureHoursET.setText(
                            viewModel.viaCitiesList.value?.let { viaCity ->
                                viaCity[outerPos].hh
                            }
                        )
                        holder.viewBinding.departureMinutesET.setText(
                            viewModel.viaCitiesList.value?.let { viaCity ->
                                viaCity[outerPos].mm
                            }
                        )
                        holder.viewBinding.journeyDayTV.setText(
                            viewModel.viaCitiesList.value?.let { viaCity ->
                                viaCity[outerPos].day
                            }
                        )
                    }
                }catch (e: Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }



            } else {
                try {
                    if (outerPos+1 <= (viewModel.viaCitiesList.value?.size?:0) - 1) {
                        holder.viewBinding.departureHoursET.setText(
                            viewModel.viaCitiesList.value?.let { viaCity->
                                viaCity[outerPos+1].hh
                            }
                        )
                        holder.viewBinding.departureMinutesET.setText(
                            viewModel.viaCitiesList.value?.let { viaCity->
                                viaCity[outerPos+1].mm
                            }
                        )
                        holder.viewBinding.journeyDayTV.setText(
                            viewModel.viaCitiesList.value?.let { viaCity->
                                viaCity[outerPos+1].day
                            }
                        )
                    }
                }catch (e: Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }

            }
        }


    }

    private fun showStageListDropDown(editText: EditText) {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(context))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val list: ArrayList<CitiesListData> = arrayListOf()
        for (i in 0 until (viewModel.stageList.value?.size?:0)) {
            val obj = CitiesListData()
            viewModel.stageList.value?.let { stageList ->
                obj.id = stageList[i].id
                obj.name = stageList[i].name
                obj.addressLine1 = stageList[i].addressLine1
                obj.addressLine2 = stageList[i].addressLine2
                obj.landmark = stageList[i].landmark
                obj.latitude = stageList[i].latitude?:""
                obj.longitude = stageList[i].longitude?:""
                obj.person = stageList[i].person
                obj.contactNumber = stageList[i].contactNumber
                obj.locationUrl = stageList[i].locationUrl
                list.add(obj)
            }

        }

        coachTypeAdapter = SourceDestinatinAdapter(context, list, this, SOURCE)
        popupBinding.searchRV.adapter = coachTypeAdapter


        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                coachTypeAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })


        stagePopupWindow = PopupWindow(
            popupBinding.root, editText.width, FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stagePopupWindow?.elevation = 12.0f;
        }


        stagePopupWindow?.showAsDropDown(editText)

        stagePopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            stagePopupWindow?.dismiss()
            true
        }
    }

    class ViewHolder(binding: AdapterStageDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val dropDownButton = binding.openIV
        val mainGroup = binding.mainG
        val deleteButton = binding.deleteIV
        val stageName = binding.stageNameTV
        val stageDropDownET = binding.stageDropDownTV
        val viewBinding = binding

    }

    override fun onDataSend(type: Int, file: Any) {
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        when (type) {
            1 -> {
                val selectedData = file as CitiesListData
                when (extra as Int) {
                    SOURCE -> {
                        stageNamee = selectedData.name
                        stageId = selectedData.id
                        list[currentPos].defaultStageId = stageId
                        list[currentPos].id = "0"
                        list[currentPos].name = stageNamee
                        list[currentPos].person = selectedData.person
                        list[currentPos].addressLine1 = selectedData.addressLine1
                        list[currentPos].addressLine2 = selectedData.addressLine2
                        list[currentPos].landmark = selectedData.landmark
                        list[currentPos].lat = selectedData.latitude
                        list[currentPos].long = selectedData.longitude
                        list[currentPos].locationUrl = selectedData.locationUrl
                        list[currentPos].contactNo = selectedData.contactNumber.substringBefore(" ")

                        showCompleteData = true
                        notifyItemChanged(currentPos)
                        stagePopupWindow?.dismiss()
                   //     showCompleteData = true


                    }

                }
            }
        }
    }

    fun isValidate(viewBinding: AdapterStageDetailsBinding):Boolean{
        if(viewBinding.stageDropDownTV.text.toString().isBlank()){
            context.toast(context.getString(R.string.please_select_stage))
        }else if(viewBinding.departureHoursET.text.toString().isBlank()){
            context.toast(context.getString(R.string.please_select_departure_hours))

        }else if(viewBinding.departureMinutesET.text.toString().isBlank()){
            context.toast(context.getString(R.string.please_select_departure_minutes))

        }else if(viewBinding.journeyDayTV.text.toString().isBlank()) {
            context.toast(context.getString(R.string.please_select_journey_day))

        }else {
            return true
        }

        return false

    }
}