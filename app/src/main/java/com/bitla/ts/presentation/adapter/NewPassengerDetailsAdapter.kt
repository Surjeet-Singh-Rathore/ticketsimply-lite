package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemCheckedListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.VarArgListener
import com.bitla.ts.databinding.ItemPassengerDetailsBinding
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.getCouponDiscount.Response.PerSeatCoupon
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.presentation.view.activity.NewPassengerDetailsActivity
import com.bitla.ts.presentation.view.activity.PassengerDetailsActivity
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.passengerList
import com.bitla.ts.utils.common.retrieveSelectedExtraSeats
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.textfield.TextInputLayout
import gone
import onChange
import timber.log.Timber
import toast
import visible
import java.util.*
import kotlin.collections.ArrayList

class NewPassengerDetailsAdapter(
    private val context: Context,
    private var passengerDetailsResult: MutableList<PassengerDetailsResult>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemCheckedListener: OnItemCheckedListener,
    private var isAdditionalFare: Boolean,
    private var isDiscountAmount: Boolean,
    private var idTypeList: MutableList<SpinnerItems> = mutableListOf(),
    private var hasPassengerContent: Boolean,
    private var hasExtraSeats: Boolean,
    private var hasExtraSeatFirstPosition: Int,
    private var isNameField: String?,
    private var isAgeField: String?,
    private var isSexField: String?,
    private var isIdTypeField: String?,
    private var isIdNumberField: String?,
    private var amountCurrency: String,
    private var currencyFormat: String,
    private var mobileNo: String,
    private var alternateMobileNo: String,
    private var isPerSeat:Boolean,
    private var couponList:ArrayList<PerSeatCoupon>,
    private var onItemPassData: OnItemPassData,
    private var hideCoupon:String,
    private var preFillData:Boolean,
    private var hideDiscount:Boolean,
    private var isMealRequired: Boolean,
    private var isMealNoType: Boolean,
    private var selectedMealTypes: Any?,
    private var freezeMealSelection: Boolean

) : RecyclerView.Adapter<NewPassengerDetailsAdapter.ViewHolder>() {

    private var name: String? = ""
    private var age: String? = ""
    private var passengerGender: String? = ""
    private var gender: String? = ""
    private var idType: String? = ""
    private var idTypeValue: String? = ""
    private var idTypePosition=0
    private var idNumber: String? = ""
    private var passportFromDate: String? = ""
    private var passportToDate: String? = ""
    private var passportPlaceOfIssue: String? = ""
    private var passengerFare: String? = ""
    private var passengerDiscount: String? = ""

    private var passengerFieldMandatory: String? = ""
    private var passengerFieldOptional: String? = ""
    private var passengerFieldHide: String? = ""
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private var isIdType = false
    private var isCheckedClick = false
    private var isFlagHeading = false
    private var extraSeatNo: String? = ""
    private var extraSeatFare: String? = ""

    var nameFiledMandatory: String = ""
    var ageFiledMandatory: String = ""
    var genderFiledMandatory: String = ""
    var idTypeFiledMandatory: String = ""
    var idNumberFiledMandatory: String = ""
    var selectedExtraSeatDetails = java.util.ArrayList<SeatDetail>()
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var allowAutoDiscount: Boolean= false
    private var allowRutDiscount: Boolean= false
    private var isFromChile: Boolean = false
    var selectedMealId: String = ""
    var selectedMealType: String = ""


//    private var isSeatFlagList: ArrayList<String> = arrayListOf()
//    private var nameSeatMap = mutableMapOf<String, String>()
//    private var ageSeatMap = mutableMapOf<String, String>()
//    private var genderSeatMap = mutableMapOf<String, String>()
//    private var idNumberSeatMap = mutableMapOf<String, String>()
//    private var idTypeSeatMap = mutableMapOf<String, String>()
//    private var mobileNumberSeatMap = mutableMapOf<String, String>()
//    private var emailSeatMap = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemPassengerDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerDetailsResult.size
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val passengerItem: PassengerDetailsResult = passengerDetailsResult[position]
        holder.setIsRecyclable(false)

        if (passengerItem.seatNumber.isNullOrEmpty()) {
            holder.seatNumber.gone()
            holder.seatNumber.text = passengerItem.seatNumber
        } else {
            holder.seatNumber.visible()
            holder.seatNumber.text = passengerItem.seatNumber
        }

        val couponName= arrayListOf<String>()
        couponList.forEach {
            couponName.add(it.coupon_name)
        }

        holder.discountCodeDrop.setAdapter(
            ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                couponName
            )
        )



       holder.discountCodeDrop.onChange {
           passengerItem.couponCode= holder.discountCodeDrop.text.toString()

           if (!holder.discountCodeDrop.text.isNullOrEmpty()){
               holder.etDiscount.text?.clear()

               holder.layoutDiscount.isEnabled= false
               holder.cross_icon_code.visible()
           }else{
               if (!holder.etDiscount.text.isNullOrEmpty()){
                   holder.etDiscount.setText("0")
               }

//               holder.layoutDiscount.isEnabled= true
               holder.cross_icon_code.gone()
           }
           holder.discountCodeDrop.tag="discount"
           onItemPassData.onItemData(holder.discountCodeDrop,
               holder.discountCodeDrop.text.toString(),passengerItem.seatNumber.toString())

       }

        if (!passengerItem.fare.isNullOrEmpty() && passengerItem.fare != "0.0")
            holder.etExtraSeatFare.setText(passengerItem.fare)
        else
            holder.etExtraSeatFare.setText("")
        //val fareAmount = "$amountCurrency${passengerItem.fare.toString()}"
        if (passengerItem.fare != null && passengerItem.fare?.isNotEmpty()!! && passengerItem.fare != "null") {
            val fareAmount =
                "$amountCurrency ${(passengerItem.fare?.toDouble())?.convert(currencyFormat)}"
            holder.fareAmount.text = fareAmount
        }
        gender = passengerItem.sex.toString()
//        idTypePosition = passengerItem.idCardType.toString()
        holder.etIdType.setText(idTypeValue, false)

        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)

        if (position > 0) {
            holder.imgDeletePassenger.visible()
        } else {
            holder.imgDeletePassenger.gone()
        }

        // check additional fare and discount
        // with single seats & single extra seats
        // and  seats with extra seats
        selectedExtraSeatDetails = retrieveSelectedExtraSeats()

        if (selectedExtraSeatDetails.size == 0) {
            if (!passengerItem.isExtraSeat) {
                checkIsAdditionalFareAndIsDiscount(holder, isAdditionalFare, isDiscountAmount)
            } else {
                additionalFareAndDiscountGone(holder)
            }
        } else {
            if (hasExtraSeatFirstPosition > position) {
                checkIsAdditionalFareAndIsDiscount(holder, isAdditionalFare, isDiscountAmount)
            } else {
                additionalFareAndDiscountGone(holder)
            }
        }

        passengerFieldMandatory = context.getString(R.string.mandatory)
        passengerFieldOptional = context.getString(R.string.optional)
        passengerFieldHide = context.getString(R.string.hide)

        if (position == 0
            && isNameField == passengerFieldMandatory
            && mobileNo != passengerFieldMandatory
            && alternateMobileNo != passengerFieldMandatory
        ) {

            holder.etName.requestFocus()
            showKeyboard(context)
        }

        if (isNameField.isNullOrEmpty())
            holder.layoutName.gone()
        else if (isNameField == passengerFieldMandatory)
            nameFiledMandatory = context.getString(R.string.mandatory)

        if (isAgeField.isNullOrEmpty())
            holder.layoutage.gone()
        else if (isAgeField == passengerFieldMandatory)
            ageFiledMandatory = context.getString(R.string.mandatory)

        if (isSexField.isNullOrEmpty())
            holder.genderContainer.gone()
        else if (isSexField == passengerFieldMandatory)
            genderFiledMandatory = context.getString(R.string.mandatory)

        if (isIdTypeField.isNullOrEmpty())
            holder.layoutIdType.gone()
        else if (isIdTypeField == passengerFieldMandatory) {
            idTypeFiledMandatory = context.getString(R.string.mandatory)
        }

        if (isIdNumberField.isNullOrEmpty())
            holder.layoutIdNumber.gone()
        else if (isIdNumberField == passengerFieldMandatory) {
            idNumberFiledMandatory = context.getString(R.string.mandatory)
        }

        passengerItem.additionalFare.let {
            holder.setItemAdditionalFare(passengerItem.additionalFare.toString())
        }

        passengerItem.discountAmount.let {
            holder.setItemAdditionalDiscount(passengerItem.discountAmount.toString())
        }

        if (idTypeValue == "Passport") {
            holder.passportLayout.visible()
        } else {
            holder.passportLayout.gone()
        }

        holder.etFromDate.setOnClickListener {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    holder.etFromDate.setText("$dayOfMonth/$monthOfYear/$year")
//                    holder.setItemPassportFromDate(passengerItem.passportIssuedDate.toString())
                }
            val dpDialog = DatePickerDialog(context, listener, year, month, day)
            dpDialog.show()
        }

        holder.etToDate.setOnClickListener {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    holder.etToDate.setText("$dayOfMonth/$monthOfYear/$year")
                }
            val dpDialog = DatePickerDialog(context, listener, year, month, day)
            dpDialog.show()
        }

        if (holder.etName.text.toString().trim().isEmpty()
            && holder.etAge.text.toString().trim().isEmpty()
            && holder.etIdType.text.toString().trim().isEmpty()
            && holder.etIdNumber.text.toString().trim().isEmpty()
            && holder.etAdditionalFare.text.toString().trim().isEmpty()
            && holder.etDiscount.text.toString().trim().isEmpty()
            && gender.toString().trim().isEmpty()
        ) {
            holder.tvFilledNotFilled.setBackgroundResource((R.drawable.shape_not_filled))
            holder.tvFilledNotFilled.text = context.getString(R.string.not_filled)


        } else {
            holder.tvFilledNotFilled.setBackgroundResource((R.drawable.button_selected_bg))
            holder.tvFilledNotFilled.text = context.getString(R.string.filled)
        }

        when {
            gender.isNullOrEmpty() -> {
            }
            gender == "Mr" -> {
                holder.passengerDetailsBtnMale.setBackgroundColor(
                    getColor(
                        context,
                        com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                    )
                )

                holder.passengerDetailsBtnFemale.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.layout_rounded_shape_border_radius_2_dp_black
                )
                holder.passengerDetailsBtnMale.setTextColor(getColor(context, R.color.white))

            }
            gender == "Ms" -> {
                holder.passengerDetailsBtnFemale.setBackgroundColor(
                    getColor(
                        context,
                        com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                    )
                )

                holder.passengerDetailsBtnMale.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.layout_rounded_shape_border_radius_2_dp_black
                )
                holder.passengerDetailsBtnFemale.setTextColor(getColor(context, R.color.white))
            }
        }

        holder.etIdType.setAdapter(
            ArrayAdapter(
                context,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                idTypeList
            )
        )

        holder.setItemPassportToDate(passengerItem.passportIssuedDate.toString())
        holder.setItemPassportFromDate(passengerItem.passportExpiryDate.toString())
        holder.setItemPassportPlaceOfIssue(passengerItem.placeOfIssue.toString())

        if (passengerItem.expand == true) {
            holder.layoutExpended.visible()
            holder.imgExpandLess.visible()
            holder.imgExpandMore.gone()
        } else {
            holder.layoutExpended.gone()
            holder.imgExpandLess.gone()
            holder.imgExpandMore.visible()
        }


        if (position == 0 && passengerDetailsResult.size > 1) {
            holder.tvCopyPassengerDetails.visible()
            holder.tvCopyPassengerDetails.isChecked = isCheckedClick
        } else {
            holder.tvCopyPassengerDetails.gone()
        }

        if (position == hasExtraSeatFirstPosition) {
            holder.tvHeadingExtraSeatDetails.visible()
        } else {
            holder.tvHeadingExtraSeatDetails.gone()
        }

        holder.imgDeletePassenger.setOnClickListener {
            holder.imgDeletePassenger.tag = context.getString(R.string.delete_passenger)
            onItemClickListener.onClick(holder.imgDeletePassenger, position)
        }

        // check passenger fields null & mandatory
        if (hasPassengerContent) {

            if (nameFiledMandatory == passengerFieldMandatory) {
                if (!passengerList[position].name.isNullOrEmpty()
                ) {
                    holder.layoutName.isErrorEnabled = false
                } else {
                    holder.layoutName.isErrorEnabled = true
                    holder.layoutName.error = "Enter Name"
                }
            }

            if (ageFiledMandatory == passengerFieldMandatory) {
                if (!passengerList[position].age.isNullOrEmpty()
                ) {
                    holder.layoutage.isErrorEnabled = false
                } else {
                    holder.layoutage.isErrorEnabled = true
                    holder.layoutage.error = "Enter Age"
                }
            }

            if (idTypeFiledMandatory == passengerFieldMandatory) {
                if (passengerList[position].idCardType.toString().isNotEmpty()
                ) {
                    holder.layoutIdType.isErrorEnabled = false
                } else {
                    holder.layoutIdType.isErrorEnabled = true
                    holder.layoutIdType.error = "Enter id type"
                }
            }

            if (idNumberFiledMandatory == passengerFieldMandatory) {
                if (passengerList[position].idCardNumber.toString().isEmpty()) {
                    holder.layoutIdNumber.isErrorEnabled = true
                    holder.layoutIdNumber.error = "Enter idNumber"
                }
            } else {
                holder.layoutIdNumber.isErrorEnabled = false
            }

            if (genderFiledMandatory == passengerFieldMandatory) {

                if (!passengerList[position].sex.isNullOrEmpty()
                ) {
                    holder.layoutMale.isErrorEnabled = false
                    holder.layoutFemale.isErrorEnabled = false
                    when {
                        passengerGender.isNullOrEmpty() -> {
                        }
                        passengerGender == "Mr" -> {
                            holder.passengerDetailsBtnMale.setBackgroundColor(
                                getColor(
                                    context,
                                    com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                                )
                            )

                            holder.passengerDetailsBtnFemale.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.layout_rounded_shape_border_radius_2_dp_black
                            )
                            holder.passengerDetailsBtnMale.setTextColor(
                                getColor(
                                    context,
                                    R.color.white
                                )
                            )

                        }
                        passengerGender == "Ms" -> {
                            holder.passengerDetailsBtnFemale.setBackgroundColor(
                                getColor(
                                    context,
                                    com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                                )
                            )

                            holder.passengerDetailsBtnMale.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.layout_rounded_shape_border_radius_2_dp_black
                            )
                            holder.passengerDetailsBtnFemale.setTextColor(
                                getColor(
                                    context,
                                    R.color.white
                                )
                            )

                        }
                    }
                } else {
                    holder.layoutMale.error = "Enter gender"
                    holder.layoutFemale.error = "Enter gender"
                    holder.layoutMale.isErrorEnabled = true
                    holder.layoutFemale.isErrorEnabled = true
                }
            }

            if (passengerItem.isExtraSeat) {
                if (!passengerList[position].fare.isNullOrEmpty() && passengerList[position].fare != "0.0"
                )
                    holder.layoutExtraSeatFare.isErrorEnabled = false
                else {
                    holder.layoutExtraSeatFare.error = "Enter seat fare"
                    holder.layoutExtraSeatFare.isErrorEnabled = true
                }

                if (!passengerList[position].seatNumber.isNullOrEmpty())
                    holder.layoutExtraSeatNo.isErrorEnabled = false
                else {
                    holder.layoutExtraSeatNo.error = "Enter seat number"
                    holder.layoutExtraSeatNo.isErrorEnabled = true
                }
            }
        }

        val layoutName = holder.layoutName
        val layoutage = holder.layoutage
//      val layoutMale = holder.layoutMale
//      val layoutFemale = holder.layoutFemale
        val genderContainer = holder.genderContainer
        val layoutIdType = holder.layoutIdType
        val layoutIdNumber = holder.layoutIdNumber
        val idContaner = holder.mainLayout2
        val fare = holder.layoutAdditionalFare
        val discount = holder.layoutDiscount

        checkPassengerHideField(
            layoutName,
            layoutage,
            genderContainer,
            layoutIdType,
            layoutIdNumber,
            idContaner,
            fare,
            discount
        )

        holder.imgExpandMore.setOnClickListener {
            holder.layoutExpended.visible()
            holder.imgExpandLess.visible()
            holder.imgExpandMore.gone()
        }

        holder.imgExpandLess.setOnClickListener {
            holder.layoutExpended.gone()
            holder.imgExpandLess.gone()
            holder.imgExpandMore.visible()
        }

        holder.passengerDetailsBtnMale.setOnClickListener {
            holder.etIdNumber.clearFocus()

            passengerGender = "Mr"//context.getString(R.string.mr)
            if (passengerGender.toString().isNotEmpty()) {
                holder.layoutMale.isErrorEnabled = false
                holder.layoutFemale.isErrorEnabled = false
            }
            passengerDetailsResult[position].sex = passengerGender.toString()

            holder.passengerDetailsBtnMale.setBackgroundColor(
                getColor(
                    context,
                    com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                )
            )
            holder.passengerDetailsBtnFemale.setBackgroundResource(R.drawable.layout_rounded_shape_border_radius_2_dp_black)
            holder.passengerDetailsBtnFemale.setHintTextColor(
                getColor(
                    context,
                    R.color.gray
                )
            )
            holder.passengerDetailsBtnMale.setHintTextColor(
                getColor(
                    context,
                    R.color.white
                )
            )
            holder.passengerDetailsBtnMale.setTextColor(getColor(context, R.color.white))
            holder.passengerDetailsBtnFemale.setTextColor(getColor(context, R.color.black))
        }

        holder.passengerDetailsBtnFemale.setOnClickListener {
            holder.etIdNumber.clearFocus()


            passengerGender = "Ms"//context.getString(R.string.ms)
            if (passengerGender.toString().isNotEmpty()) {
                holder.layoutMale.isErrorEnabled = false
                holder.layoutFemale.isErrorEnabled = false
            }
            passengerDetailsResult[position].sex = passengerGender.toString()

            holder.passengerDetailsBtnFemale.setBackgroundColor(
                getColor(
                    context,
                    com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                )
            )
            holder.passengerDetailsBtnFemale.setHintTextColor(
                getColor(
                    context,
                    R.color.white
                )
            )
            holder.passengerDetailsBtnMale.setHintTextColor(
                getColor(
                    context,
                    R.color.gray
                )
            )
            holder.passengerDetailsBtnMale.setBackgroundResource(R.drawable.layout_rounded_shape_border_radius_2_dp_black)
            holder.passengerDetailsBtnFemale.setTextColor(getColor(context, R.color.white))
            holder.passengerDetailsBtnMale.setTextColor(getColor(context, R.color.black))

        }

        holder.setItemName(passengerItem.name)
        holder.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name = s.toString()
                passengerDetailsResult[position].name = s.toString()

                if (!name.isNullOrEmpty())
                    layoutName.isErrorEnabled = false
                else {
                    if (nameFiledMandatory == passengerFieldMandatory) {
                        layoutName.isErrorEnabled = true
                        layoutName.error = "enter name"
                    }
                }
            }
        })

        holder.setItemAge(passengerItem.age)
        holder.etAge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                age = s.toString()
                passengerDetailsResult[position].age = s.toString()

                if (!age.isNullOrEmpty())
                    layoutage.isErrorEnabled = false
                else {
                    if (ageFiledMandatory == passengerFieldMandatory) {
                        layoutage.isErrorEnabled = true
                        layoutage.error = "enter age"
                    }
                }
            }
        })


        holder.etIdType.setOnItemClickListener { parent, _, positions, _ ->

            idTypeValue = parent.getItemAtPosition(positions).toString()
            idTypePosition = idTypeList[parent.getItemIdAtPosition(positions).toInt()].id
            passengerDetailsResult[position].idCardType = idTypePosition.toString()
            if (!idTypeValue.equals("rut", true)){
                passengerItem.idType= idTypeValue
                holder.etDiscount.setText("0")

                holder.layoutDiscount.isEnabled= true
                holder.perSeatDiscount.isEnabled= true
                holder.discountCodeDrop.setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        couponName
                    )
                )
            }else{
                holder.etIdNumber.text?.clear()
            }
            if (idTypeValue == "Passport")
                holder.passportLayout.visible()
            else
                holder.passportLayout.gone()


            if (!idType.isNullOrEmpty())
                layoutIdType.isErrorEnabled = false
            else {
                if (idTypeFiledMandatory == passengerFieldMandatory) {
                    if (idTypeValue == "") {
                        layoutIdType.isErrorEnabled = true
                        layoutIdType.error = "enter idType"
                    }
                }
            }
        }

        holder.etIdNumber.setOnFocusChangeListener { view, b ->
            if (b){
                holder.etIdNumber.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {

                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        idNumber = s.toString()
                        passengerDetailsResult[position].idCardNumber = s.toString()

                        if (!idNumber.isNullOrEmpty()){
                            layoutIdNumber.isErrorEnabled = false

                            if (holder.etIdType.text.toString() .equals("rut",true)){
                                if (idNumber.toString().length==10){
                                    idTypeList.forEach {
                                        if (it.value.equals("rut",true)){
                                            idTypePosition= it.id
                                        }
                                    }

                                    passengerItem.idnumber= holder.etIdNumber.text.toString()
                                    onItemPassData.onItemDataMore(holder.etIdNumber,passengerItem.seatNumber!!,holder.etIdNumber.text.toString(),idTypePosition.toString())
                                }
                            }
                        }
                        else {
                            if (idNumberFiledMandatory == passengerFieldMandatory) {
                                layoutIdNumber.isErrorEnabled = true
                                layoutIdNumber.error = "enter id number"
                            }
                        }
                    }
                })

            }
            else{
                holder.etIdNumber.tag= "idNum"
                if (idTypeValue.equals("rut",true)){
                    idTypeList.forEach {
                        if (it.value.equals("rut",true)){
                            idTypePosition= it.id
                        }
                    }
                    passengerItem.idType= idTypeValue
                    if (holder.etIdNumber.text.toString().length!=10){
                        holder.etIdNumber.requestFocus()
                        onItemPassData.onItemDataMore(holder.etIdNumber,passengerItem.seatNumber!!,holder.etIdNumber.text.toString(),idTypePosition.toString())
                    }
                }
            }
        }
//        holder.setItemIdNumber(passengerItem.idCardNumber.toString())

        holder.etAdditionalFare.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passengerFare = s.toString()
                try {
//                    passengerDetailsResult[position].additionalFare = s.toString().toIntOrNull()
                } catch (ex: NumberFormatException) {
                }
            }
        })



        holder.ivApplyAdditionalFare.setOnClickListener {
            if(holder.etAdditionalFare.text!!.isEmpty() || holder.etAdditionalFare.text!!.toString().toInt()==0){
                context.toast("Please enter valid fare")
            }else{
                it.tag=context.getString(R.string.apply_additional_fare)
                passengerDetailsResult[position].additionalFare = holder.etAdditionalFare.text.toString()
                holder.ivRemoveAdditionalFare.visible()
                holder.ivApplyAdditionalFare.gone()
                holder.etAdditionalFare.isFocusable=false
                holder.etAdditionalFare.isFocusableInTouchMode=false

                onItemClickListener.onClick(holder.ivApplyAdditionalFare,position)
            }
        }

        holder.ivRemoveAdditionalFare.setOnClickListener {
            it.tag=context.getString(R.string.remove_additional_fare)
            holder.ivApplyAdditionalFare.visible()
            holder.ivRemoveAdditionalFare.gone()
            holder.etAdditionalFare.isFocusable=true
            holder.etAdditionalFare.isFocusableInTouchMode=true
            holder.etAdditionalFare.setText("0")
            passengerDetailsResult[position].additionalFare = "0"
            onItemClickListener.onClick(holder.ivRemoveAdditionalFare,position)
        }






        holder.etDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passengerDiscount = s.toString()
                try {
//                    passengerDetailsResult[position].discountAmount =
//                        passengerDiscount.toString().toIntOrNull()
                } catch (ex: NumberFormatException) {
                }
            }
        })


        holder.ivApplyDiscountFare.setOnClickListener {
            if(holder.etDiscount.text!!.isEmpty() || holder.etDiscount.text!!.toString().toInt()==0){
                context.toast("Please enter valid fare")
            }else{
                it.tag=context.getString(R.string.apply_discount_amount)
                passengerDetailsResult[position].discountAmount = holder.etDiscount.text!!.toString()
                holder.ivRemoveDiscountFare.visible()
                holder.ivApplyDiscountFare.gone()
                holder.etDiscount.isFocusable=false
                holder.etDiscount.isFocusableInTouchMode=false

                onItemClickListener.onClick(holder.ivApplyDiscountFare,position)
            }
        }

        holder.ivRemoveDiscountFare.setOnClickListener {
            it.tag=context.getString(R.string.remove_discount_fare)
            holder.ivApplyDiscountFare.visible()
            holder.ivRemoveDiscountFare.gone()
            holder.etDiscount.isFocusable=true
            holder.etDiscount.isFocusableInTouchMode=true
            holder.etDiscount.setText("0")
            passengerDetailsResult[position].discountAmount = ""
            onItemClickListener.onClick(holder.ivRemoveDiscountFare,position)
        }

        holder.setItemSeatNumber(passengerItem.seatNumber.toString())

        if (passengerItem.isExtraSeat) {
            holder.extraBookingLayout.visible()

            holder.etExtraSeatNo.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    extraSeatNo = s.toString()
                    passengerDetailsResult[position].seatNumber = s.toString()
                    holder.seatNumber.text = extraSeatNo

                    if (!extraSeatNo.isNullOrEmpty()) {
                        holder.layoutExtraSeatNo.isErrorEnabled = false
                        holder.seatNumber.visible()
                    } else {
                        holder.seatNumber.gone()
                        holder.layoutExtraSeatNo.error = "seat number"
                        holder.layoutExtraSeatNo.isErrorEnabled = true
                    }
                }
            })

            holder.etExtraSeatFare.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    extraSeatFare = s.toString()
                    passengerDetailsResult[position].fare = s.toString()
                    var fareAmount = ""
                    //  val fareAmount = "$amountCurrency $extraSeatFare"
                    if (extraSeatFare != null && extraSeatFare?.isNotEmpty() == true) {
                        fareAmount =
                            "$amountCurrency ${(extraSeatFare?.toDouble())?.convert(currencyFormat)}"
                    }
                    holder.fareAmount.text = fareAmount
                    holder.etExtraSeatFare.tag = context.getString(R.string.extra_seat_fare)
                    onItemClickListener.onClick(holder.etExtraSeatFare, position)
                    if (!extraSeatFare.isNullOrEmpty()) {
                        holder.tvFilledNotFilled.setBackgroundResource((R.drawable.button_selected_bg))
                        holder.tvFilledNotFilled.text = context.getString(R.string.filled)
                        holder.layoutExtraSeatFare.isErrorEnabled = false
                    } else {
                        holder.tvFilledNotFilled.setBackgroundResource((R.drawable.shape_not_filled))
                        holder.tvFilledNotFilled.text = context.getString(R.string.not_filled)
                        holder.layoutExtraSeatFare.error = "seat fare"
                        holder.layoutExtraSeatFare.isErrorEnabled = true
                    }
                }
            })

        } else {
            holder.extraBookingLayout.gone()
        }

        if (passengerItem.isMealSelected == true) {
            holder.checkMeal.gone()
            holder.uncheckMeal.visible()
        }else
        {
            holder.checkMeal.visible()
            holder.uncheckMeal.gone()
        }

        holder.checkMeal.setOnClickListener {
            holder.checkMeal.gone()
            holder.uncheckMeal.visible()
            passengerItem.isMealSelected = false
            passengerItem.selectedMealType = selectedMealType
            holder.layoutMealType.gone()
            passengerDetailsResult[position].mealRequired = false
            passengerDetailsResult[position].selectedMealType = ""
        }

        holder.uncheckMeal.setOnClickListener {
            holder.checkMeal.visible()
            holder.uncheckMeal.gone()
            passengerItem.isMealSelected = true

            passengerDetailsResult[position].mealRequired = true

            if (!isMealNoType) {
                showMealDropdown(holder.layoutMealType, holder.autoMealType, generateMealList(), position)
            }
            else
                holder.layoutMealType.gone()
        }

//        Timber.d("freezeMealSelection:: $freezeMealSelection - $isMealRequired")

        if (isMealRequired) {
            holder.checkMealTitle.visible()
            val mealList = generateMealList()
            holder.checkMeal.isEnabled = !freezeMealSelection
            holder.checkMeal.visible()
            holder.uncheckMeal.gone()

            if (holder.checkMeal.isVisible && !isMealNoType) {
                showMealDropdown(holder.layoutMealType, holder.autoMealType, mealList, position)
            } else {
                holder.layoutMealType.gone()
            }

            holder.autoMealType.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, itemPosition, id ->
                    selectedMealId = mealList[itemPosition].id.toString()
                    selectedMealType = mealList[itemPosition].value
                    passengerDetailsResult[position].mealRequired = true
                    passengerDetailsResult[position].selectedMealType = selectedMealId
                }

        } else {
            holder.layoutMealType.gone()
            holder.checkMealTitle.gone()
            holder.checkMeal.gone()
            holder.uncheckMeal.gone()
        }

        if (passengerItem.isExtraSeat){
            holder.apply {
                layoutMealType.gone()
                checkMealTitle.gone()
                checkMeal.gone()
                uncheckMeal.gone()
            }
        }

        if (isMealRequired && isMealNoType && !passengerItem.isExtraSeat ){
            passengerDetailsResult[position].mealRequired = true
        }


        Timber.d("hideCouponCheck:: $hideCoupon , $isPerSeat, $preFillData, ${passengerItem.idType}")

        if (isPerSeat){
            holder.perSeatDiscount.visible()
                if (preFillData){
                    Timber.d("flowCheck:0")

                    if (PreferenceUtils.getPrefillData()!=null){
                        val prefill= PreferenceUtils.getPrefillData()
                        if (prefill!!.passenger_seat== passengerItem.seatNumber){
                            idTypeList.forEach {
                                if (it.value.equals("rut",true)){
                                    passengerItem.idType= it.value
                                    holder.etIdType.setText(it.value, false)
                                }
                            }
                            if (hideCoupon!= "") {
                                Timber.d("flowCheck:1")

                                passengerItem.couponCode= hideCoupon
                                holder.discountCodeDrop.visible()
                                holder.perSeatDiscount.isEnabled= false
                                holder.etDiscount.text?.clear()
                                holder.layoutDiscount.isEnabled= false
                                holder.cross_icon_code.visible()
                            }else{
                                Timber.d("flowCheck:2")

                                passengerItem.couponCode= hideCoupon
                                holder.cross_icon_code.gone()
                                holder.layoutDiscount.isEnabled= true
                                holder.perSeatDiscount.isEnabled= true
                                holder.etDiscount.setText("0")
                            }
                            holder.discountCodeDrop.setText(passengerItem.couponCode)
                            passengerItem.couponCode= holder.discountCodeDrop.text.toString()
                            holder.etIdNumber.setText(prefill.rutNumber)
                            holder.etAge.setText(prefill.passenger_age)
                            holder.etName.setText(prefill.passenger_name)
                        }else{
                            Timber.d("flowCheck:3")

                            if (hideCoupon!= "") {
                                holder.discountCodeDrop.visible()
                                passengerItem.couponCode=""
                                holder.perSeatDiscount.isEnabled= false
                                holder.etDiscount.text?.clear()
                                holder.layoutDiscount.isEnabled= false
                                holder.cross_icon_code.gone()

                            }else{
                                Timber.d("flowCheck:5")
                                passengerItem.couponCode= hideCoupon
                                holder.cross_icon_code.gone()
                                holder.layoutDiscount.isEnabled= true
                                holder.perSeatDiscount.isEnabled= true
                                holder.etDiscount.setText("0")
                            }
                            holder.etIdType.setText(passengerItem.idType)
                            holder.etIdNumber.setText(passengerItem.idnumber)
                            holder.discountCodeDrop.setText(passengerItem.couponCode)
                        }
                    }else{

                        holder.etIdType.setText(passengerItem.idType)

                    }
                }else{
                    holder.discountCodeDrop.setText(passengerItem.couponCode)
                }
        }else{
                if (preFillData){
                    if (PreferenceUtils.getPrefillData()!=null){
                        var prefill= PreferenceUtils.getPrefillData()
                        if (prefill!!.passenger_seat== passengerItem.seatNumber){
                            idTypeList.forEach {
                                if (it.value.equals("rut",true)){
                                    holder.etIdType.setText(it.value, false)
                                }
                            }
                            holder.etIdNumber.setText(prefill.rutNumber)
                            holder.etAge.setText(prefill.passenger_age)
                            holder.etName.setText(prefill.passenger_name)
                        }else{
                            if (!holder.discountCodeDrop.text.isNullOrEmpty()){
                                holder.cross_icon_code.visible()
                            }else{
                                holder.cross_icon_code.gone()
                            }
                            holder.etIdNumber.setText(passengerItem.idnumber)
                            holder.etIdType.setText(passengerItem.idType)
                            holder.discountCodeDrop.setText(passengerItem.couponCode)
                        }
                    }
                }
            Timber.d("flowCheck:6")

            if (hideDiscount){
                holder.etDiscount.text?.clear()
                holder.layoutDiscount.isEnabled= false
            }else{
                holder.etDiscount.setText("0")
                holder.layoutDiscount.isEnabled= true
            }
        }

        holder.cross_icon_code.setOnClickListener {
            passengerDetailsResult.forEach {
                holder.perSeatDiscount.isEnabled= true
                holder.etDiscount.setText("0")
                holder.layoutDiscount.isEnabled= true
                if (idType.equals("rut", true) || idType.isNullOrEmpty()){
                    it.idType= ""
                    it.idnumber=""
//                    it.idCardNumber=""
                }

                preFillData= false
                hideCoupon=""

                notifyDataSetChanged()
            }
            holder.cross_icon_code.gone()
            holder.discountCodeDrop.text.clear()

            holder.cross_icon_code.tag= "clearCode"
            onItemPassData.onItemData(holder.cross_icon_code, passengerItem.seatNumber.toString(), holder.etIdNumber.text.toString())
        }


//        if (enableField) {
//            holder.perSeatDiscount.isEnabled = true
//
//        }

        holder.etIdType.setAdapter(
            ArrayAdapter(
                context,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                idTypeList
            )
        )


        holder.tvCopyPassengerDetails.setOnCheckedChangeListener { _, isChecked ->
            onItemCheckedListener.onItemChecked(true,holder.tvCopyPassengerDetails,0)
//            dialog.show()
            copyPassengerDetails(holder, isChecked, idTypeValue)
        }

        if (allowRutDiscount){
            if (isPerSeat){
                holder.perSeatDiscount.visible()
            }else{
                holder.perSeatDiscount.gone()
            }
        }else{
            holder.perSeatDiscount.gone()
        }




    }

    private fun generateMealList(): MutableList<SpinnerItems> {
        val mealList: MutableList<SpinnerItems> = mutableListOf()
        if (selectedMealTypes != null) {
            val mealOriginalList = selectedMealTypes as List<List<Any?>?>?
            if (!mealOriginalList.isNullOrEmpty()) {
                for (i in 0..mealOriginalList.size.minus(1)) {
                    val id: Int = mealOriginalList[i]!![0].toString().toDouble().toInt()
                    val meal: String = mealOriginalList[i]!![1].toString()
                    mealList.add(SpinnerItems(id, meal))
                }
            }
        }
        return mealList
    }

    private fun showMealDropdown(
        layoutMealType: TextInputLayout,
        autoMealType: AutoCompleteTextView,
        mealList: MutableList<SpinnerItems>,
        position: Int
    ) {
        layoutMealType.visible()
        if (!isCheckedClick) {
            selectedMealId = mealList[0].id.toString()
            selectedMealType = mealList[0].value
        }

        passengerDetailsResult[position].isMealSelected = true
        passengerDetailsResult[position].mealRequired = true
        passengerDetailsResult[position].selectedMealType = selectedMealId

        autoMealType.setText(selectedMealType)

        autoMealType.setAdapter(
            ArrayAdapter(
                context,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                mealList
            )
        )


    }

    inner class ViewHolder(binding: ItemPassengerDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layoutExpended = binding.passengerDetailsLayoutExpended
        val imgExpandMore = binding.imgExpandMore
        val imgExpandLess = binding.imgExpandLess
        val seatNumber = binding.passengerDetailsSeatNumber
        val fareAmount = binding.passengerDetailsFareAmount
        val imgDeletePassenger = binding.imgDeletePassenger
        val tvCopyPassengerDetails = binding.tvCopyPassengerDetails
        val etName = binding.etName
        val etAge = binding.etAge
        val etIdType = binding.etIdType
        val etIdNumber = binding.etIdNumber
        val etAdditionalFare = binding.etAdditionalFare
        val ivApplyAdditionalFare = binding.applyAdditionalFareIV
        val ivRemoveAdditionalFare = binding.removeAdditionalFareIV
        val ivApplyDiscountFare = binding.applyDiscountFareIV
        val ivRemoveDiscountFare = binding.removeDiscountFareIV
        val etDiscount = binding.etDiscount
        val etFromDate = binding.etFromDate
        val etToDate = binding.etToDate
        val etPlaceIssue = binding.etPlaceIssue
        val etExtraSeatFare = binding.etExtraSeatFare
        val etExtraSeatNo = binding.etExtraSeatNo

        val passengerDetailsBtnMale = binding.passengerDetailsBtnMale
        val passengerDetailsBtnFemale = binding.passengerDetailsBtnFemale
        val tvFilledNotFilled = binding.tvFilledNotFilled

        var layoutName = binding.layoutName
        var layoutage = binding.layoutAge
        var layoutMale = binding.layoutMale
        var layoutFemale = binding.layoutFemale
        var genderContainer = binding.genderContainer
        var layoutIdType = binding.layoutType
        var layoutIdNumber = binding.layoutIdNumber
        var layoutAdditionalFare = binding.layoutAdditionalFare
        var layoutDiscount = binding.layoutDiscount
        var tvAdditionalNdDiscountHeading = binding.tvAdditionalNdDiscountHeading
        var mainLayout2 = binding.mainLayout2
        var mainLayout4 = binding.mainLayout4
        var containerAdditionalDiscount = binding.containerAdditionalDiscount
        var perSeatDiscount = binding.discountCouponLayout
        var discountCodeDrop = binding.dicountCodeDrop
        var cross_icon_code = binding.crossClickAdapter

        //      var additionalFareDiscountLayout = binding.mainLayout4
        var extraBookingLayout = binding.mainLayout5
        var passportLayout = binding.mainLayout3
        var layoutExtraSeatFare = binding.layoutExtraSeatFare
        var layoutExtraSeatNo = binding.layoutExtraSeatNo
        var tvHeadingExtraSeatDetails = binding.tvHeadingExtraSeatDetails

        var layoutMealType = binding.layoutMealType
        var checkMealTitle = binding.checkMealTitle
        var autoMealType = binding.autoMealType

        var checkMeal = binding.checkMeal
        var uncheckMeal = binding.uncheckMeal

        fun setItemName(name: CharSequence?) = etName.setText(name)

        fun setItemAge(age: CharSequence?) = etAge.setText(age)

        fun setItemGender(gender: CharSequence?) = passengerDetailsBtnMale.setText(gender)

        fun setItemIdType(idType: CharSequence?) = etIdType.setText(idType)

        fun setItemIdNumber(idNumber: CharSequence?) = etIdNumber.setText(idNumber)

        fun setItemAdditionalFare(additionalFare: CharSequence?) = etAdditionalFare.setText(additionalFare)

        fun setItemAdditionalDiscount(additionalDiscount: CharSequence?) = etDiscount.setText(additionalDiscount)

        fun setItemPassportToDate(toDate: CharSequence?) = etToDate.setText(toDate)

        fun setItemPassportFromDate(fromDate: CharSequence?) = etFromDate.setText(fromDate)

        fun setItemPassportPlaceOfIssue(toDate: CharSequence?) = etPlaceIssue.setText(toDate)

        fun setItemSeatNumber(seatNumber: CharSequence?) = etExtraSeatNo.setText(seatNumber)
    }

    private fun checkPassengerHideField(
        name: TextInputLayout,
        age: TextInputLayout,
        genderContainer: LinearLayout,
        idType: TextInputLayout,
        idNumber: TextInputLayout,
        idContainer: ConstraintLayout,
        fare: TextInputLayout,
        discount: TextInputLayout,
    ) {

        if (isNameField == passengerFieldHide)
            name.gone()
        if (isAgeField == passengerFieldHide)
            age.gone()
        if (isSexField == passengerFieldHide)
            genderContainer.gone()
        if (isIdTypeField == passengerFieldHide)
            idType.gone()
        if (isIdNumberField == passengerFieldHide)
            idNumber.gone()
        if (isIdTypeField == passengerFieldHide
            && isIdNumberField == passengerFieldHide)
            idContainer.gone()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun copyPassengerDetails(holder: ViewHolder, isChecked: Boolean, idTypeValue:String?) {


        if (passengerDetailsResult[0].name.toString().isEmpty()
            && passengerDetailsResult[0].age.toString().isEmpty()
            && passengerDetailsResult[0].additionalFare=="0"
            && passengerDetailsResult[0].discountAmount==""
            && passengerDetailsResult[0].sex.toString().isEmpty()
        ) {
            holder.tvCopyPassengerDetails.isChecked = false
            context.toast("Primary passenger field's empty")

        } else {
            if (isChecked) {
                if (holder.checkMeal.isVisible)
                {
                    passengerDetailsResult.forEach {
                        it.isMealSelected = false
                        it.selectedMealType = selectedMealType
                    }
                }else
                {
                    passengerDetailsResult.forEach {
                        it.isMealSelected = true
                        it.selectedMealType = ""
                    }
                }

                isCheckedClick = true
                isIdType = true

                name = holder.etName.text.toString()
                age = holder.etAge.text.toString()
                idType = holder.etIdType.text.toString()
                idNumber = holder.etIdNumber.text.toString()
                passengerDetailsResult.forEach {
                    it.idnumber= idNumber
                    it.idType= idType}
                passportToDate = holder.etToDate.text.toString()
                passportFromDate = holder.etFromDate.text.toString()
                passportPlaceOfIssue = holder.etPlaceIssue.text.toString()

                passengerFare = holder.etAdditionalFare.text.toString()
                passengerDiscount = holder.etDiscount.text.toString()
                gender = passengerGender
                selectedMealType = holder.autoMealType.text.toString()

                passengerDetailsResult.apply {

                    for (i in 0 until passengerDetailsResult.size) {

                        this[i].passportIssuedDate = passportToDate.toString()
                        this[i].passportExpiryDate = passportFromDate.toString()
                        this[i].placeOfIssue = passportPlaceOfIssue.toString()

                        if (i >= 0) {
                            this[i].name = name.toString()
                            this[i].age = age.toString()
                            this[i].sex = gender.toString()
//                            passengerDetailsResult[i].idCardType = idTypeValue?.toIntOrNull()
                            this[i].idCardNumber = idNumber.toString()

                            if (passengerFare != null && passengerFare!!.isNotEmpty())
                                this[i].additionalFare = passengerFare

                            if (passengerDiscount != null && passengerDiscount!!.isNotEmpty())
                                this[i].discountAmount = passengerDiscount.toString()

                            holder.etIdType.setAdapter(
                                ArrayAdapter(
                                    context,
                                    R.layout.spinner_dropdown_item,
                                    R.id.tvItem,
                                    idTypeList
                                )
                            )
                        }
                    }
                }

                holder.apply {
                    setItemName(name)
                    setItemAge(age)
                    setItemPassportToDate(passportToDate)
                    setItemPassportFromDate(passportFromDate)
                    setItemPassportPlaceOfIssue(passportPlaceOfIssue)

//                    if (!idType.equals("rut", true)){
                        setItemIdType(idType)
                        setItemIdNumber(idNumber)
//                    }

                    setItemAdditionalFare(passengerFare)
                    setItemAdditionalDiscount(passengerDiscount)
                    setItemGender(gender)
                }

                notifyDataSetChanged()

            } else {
                isCheckedClick = false

                passengerDetailsResult.forEachIndexed { i, it->
                    if (i > 0) {
                        it.isMealSelected = true
                        it.selectedMealType = ""
                    }
                }

                passengerDetailsResult.apply {

                    for (i in 0 until passengerDetailsResult.size) {
                        if (i == 0) {
                            this[i].name = name.toString()
                            this[i].age = age.toString()
                            this[i].sex = gender.toString()
                            this[i].idCardType = idTypeValue?.toIntOrNull().toString()
                            this[i].idCardNumber = idNumber.toString()
                            this[i].passportIssuedDate = passportToDate.toString()
                            this[i].passportExpiryDate = passportFromDate.toString()
                            this[i].placeOfIssue = passportPlaceOfIssue.toString()

                            if (passengerFare != null && passengerFare!!.isNotEmpty())
                                passengerDetailsResult[i].additionalFare =
                                    passengerFare

                            if (passengerDiscount != null && passengerDiscount!!.isNotEmpty()) {
                                passengerDetailsResult[i].discountAmount =
                                    passengerDiscount.toString()
                            }

                        } else {
                            this[i].idnumber= ""
                            this[i].idType= ""
                            this[i].name = ""
                            this[i].age = ""
                            this[i].sex = ""
                            this[i].idCardType = "".toIntOrNull().toString()
                            this[i].idCardNumber = ""
                            this[i].passportIssuedDate = ""
                            this[i].passportExpiryDate = ""
                            this[i].placeOfIssue = ""
                            this[i].additionalFare = "0"
                            this[i].discountAmount = ""
                            this[i].sex = ""
                            this[i].mealRequired = false
                            this[i].selectedMealType = ""
                        }
                    }
                }
                holder.apply {
                    setItemName(name)
                    setItemAge(age)
                    setItemPassportToDate(passportToDate)
                    setItemPassportFromDate(passportFromDate)
                    setItemPassportPlaceOfIssue(passportPlaceOfIssue)

//                    if (!idType.equals("rut", true)) {
                        setItemIdType(idType)
                        setItemIdNumber(idNumber)
//                    }

                    setItemAdditionalFare(passengerFare)
                    setItemAdditionalDiscount(passengerDiscount)
                    setItemGender(gender)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun showKeyboard(mContext: Context) {
        val inputMethodManager: InputMethodManager =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }


    private fun checkIsAdditionalFareAndIsDiscount(holder: ViewHolder,isAdditionalFare: Boolean,isDiscountAmount: Boolean){
        if (isAdditionalFare && !isDiscountAmount) {
            holder.apply {
                tvAdditionalNdDiscountHeading.text = context.getString(R.string.additional_fare_discount)
                mainLayout4.visible()
                containerAdditionalDiscount.visible()
                tvAdditionalNdDiscountHeading.visible()
            }

        } else if (!isAdditionalFare && isDiscountAmount) {
            holder.apply {
                mainLayout4.gone()
                containerAdditionalDiscount.gone()
                tvAdditionalNdDiscountHeading.gone()
            }
        } else {
            if (isAdditionalFare) {
                holder.apply {
                    mainLayout4.visible()
                    tvAdditionalNdDiscountHeading.visible()
                    containerAdditionalDiscount.visible()
                    layoutAdditionalFare.visible()
                    tvAdditionalNdDiscountHeading.text = context.getString(R.string.additional_fare)
                }
            } else {
                holder.layoutAdditionalFare.gone()
            }

            if (!isDiscountAmount) {
                holder.apply {
                    mainLayout4.visible()
                    tvAdditionalNdDiscountHeading.visible()
                    containerAdditionalDiscount.visible()
                    layoutDiscount.visible()
                    tvAdditionalNdDiscountHeading.text = context.getString(R.string.discount)
                }
            } else {
                holder.layoutDiscount.gone()
            }
        }
    }

    private fun additionalFareAndDiscountGone(holder: ViewHolder){
        holder.apply {
            tvAdditionalNdDiscountHeading.gone()
            mainLayout4.gone()
            containerAdditionalDiscount.gone()
            tvAdditionalNdDiscountHeading.gone()
        }
    }
}