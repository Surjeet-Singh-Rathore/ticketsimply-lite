package com.example.buscoach

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.underline
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.buscoach.base.BaseClass.getAllSeatSelection
import com.example.buscoach.databinding.FragmentCoachBinding
import com.example.buscoach.listeners.SeatSelectListener
import com.example.buscoach.multistation_data.MultiHopSeatDetail
import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.service_details_response.ServiceDetailsModel
import com.example.buscoach.utils.Const
import com.example.buscoach.utils.Const.Companion.DR_IMG
import com.example.buscoach.utils.Const.Companion.FRAGMENT_LEFT
import com.example.buscoach.utils.Const.Companion.FRAGMENT_RIGHT
import com.example.buscoach.utils.Const.Companion.HORIZONTAL_SLEEPER
import com.example.buscoach.utils.Const.Companion.IMAGE_ICON
import com.example.buscoach.utils.Const.Companion.PA_IMG
import com.example.buscoach.utils.Const.Companion.SEATER
import com.example.buscoach.utils.Const.Companion.SM_IMG
import com.example.buscoach.utils.Const.Companion.ST_IMG
import com.example.buscoach.utils.Const.Companion.TV_IMG
import com.example.buscoach.utils.Const.Companion.VERTICAL_SLEEPER
import com.example.buscoach.utils.Const.Companion.WHITE_COLOR
import com.example.buscoach.utils.Const.Companion.WR_IMG
import java.util.Locale

class CoachFragment : Fragment() {

    private val TAG: String = CoachViewModel::class.java.simpleName

    private lateinit var binding: FragmentCoachBinding

    private var selectedSeatColor: String? = null
    private var isAllSeatsSelection: Boolean = false
    private var seatDetails = listOf<SeatDetail>()
    private var serviceDetailsModel: ServiceDetailsModel? = null
    private lateinit var onSeatSelectListener: SeatSelectListener

    private var fragmentPosition: Int = FRAGMENT_LEFT
    private lateinit var coachViewModel: CoachViewModel
    private var seatDetailsMap = hashMapOf<SeatDetail, View>()
    private var showUpperCoachTypeButton: Boolean = false
    private var currentVisibleCoachType = Const.LOWER_COACH
    private lateinit var multiHopSeatSamePnrGroupAdapter: MultiHopSeatSamePnrGroupParentAdapter
    private val multiHopChildAdapterMap = LinkedHashMap<Int, MultiHopSeatSamePnrGroupChildAdapter>()

    companion object {
        private const val ARG_POSITION = "position"
        private const val SERVICE_DETAILS_MODEL = "service_details_model"
        private const val SELECTED_SEAT_COLOR = "selected_seat_color"

        fun newInstance(
            fragmentPosition: Int,
            serviceDetailsModel: ServiceDetailsModel,
            selectedSeatColor: String?
        ): CoachFragment {
            val fragment = CoachFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, fragmentPosition)
            args.putSerializable(SERVICE_DETAILS_MODEL, serviceDetailsModel)
            args.putSerializable(SELECTED_SEAT_COLOR, selectedSeatColor)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onSeatSelectListener = parentFragment as SeatSelectListener
        } catch (e: ClassCastException) {
            //throw ClassCastException("Error in retrieving data. Please try again")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {

            fragmentPosition = arguments?.getInt(ARG_POSITION) ?: FRAGMENT_RIGHT
            serviceDetailsModel =
                arguments?.getSerializable(SERVICE_DETAILS_MODEL) as ServiceDetailsModel
            selectedSeatColor = arguments?.getString(SELECTED_SEAT_COLOR)
            binding = FragmentCoachBinding.inflate(layoutInflater)

            /*serviceDetailsModel = if (fragmentPosition == FRAGMENT_RIGHT) {
            BaseClass.getRightCoachResponse()
        } else {
            BaseClass.getLeftCoachResponse()
        }*/
            isAllSeatsSelection = getAllSeatSelection()

            coachViewModel = ViewModelProvider(this)[CoachViewModel::class.java]

            setCoachHeader()

            if (serviceDetailsModel != null) {
                seatDetails = serviceDetailsModel?.body?.coachDetails?.seatDetails ?: listOf()
                coachViewModel.getSelectedSeats(isAllSeatsSelection, seatDetails)
                coachViewModel.getSelectedSeatColor(serviceDetailsModel!!)

                for (i in seatDetails.indices) {
                    val frameLayout = LayoutInflater.from(requireContext())
                        .inflate(R.layout.grid_item, null) as FrameLayout
                    val imageView: ImageView = frameLayout.findViewById(R.id.imageView)
                    val textView: TextView = frameLayout.findViewById(R.id.tvTop)

                    if (seatDetails[i].isGangway == false) seatClickListenerNew(frameLayout, i)

                    drawCoach(
                        seatDetails[i],
                        binding.gridLayout,
                        binding.gridLayoutUpper,
                        frameLayout,
                        imageView,
                        textView
                    )

                    setColorForAllSeats(imageView, textView, seatDetails[i])
                    /*if (showUpperCoachTypeButton) {
                    binding.radioGroup.visibility = View.VISIBLE
                } else {
                    binding.radioGroup.visibility = View.GONE
                }*/
                }

                selectPreviouslySelectedSeats()

                if (!showUpperCoachTypeButton) {
                    setNoBerthTextToUpperRadioGroup()
                }
            }

            binding.rbLower.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    onLowerTabSelection()
                }
            }

            binding.rbUpper.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    onUpperTabSelection()
                }
            }

            binding.tvDone.setOnClickListener {
                onMultiHopDoneClick()
            }

            binding.tvGoBack.setOnClickListener {
                onMultiHopOnCancelClick()
            }
            //setRecommendedSeats(listOf("3", "4"))
        }
        return binding.root
    }

    private fun setColorForAllSeats(
        imageView: ImageView?,
        textView: TextView,
        seatDetail: SeatDetail
    ) {
        if (isAllSeatsSelection) {
            selectedTintColor(imageView, textView, seatDetail)
        } else {
            unSelectedTintColor(imageView, textView, seatDetail)
        }
    }

    private fun unSelectedTintColor(
        imageView: ImageView?,
        textView: TextView,
        seatDetail: SeatDetail
    ) {
        updateTintColorNew(seatDetail, imageView, textView, isSelected = false)
    }

    private fun selectedTintColor(
        imageView: ImageView?,
        textView: TextView,
        seatDetail: SeatDetail
    ) {
        updateTintColorNew(seatDetail, imageView, textView, isSelected = true)
    }

    private fun updateTintColorNew(
        seatDetail: SeatDetail,
        imageView: ImageView?,
        textView: TextView,
        isSelected: Boolean
    ) {
        seatDetail.isSelected = isSelected
        imageView?.clearAnimation()
        when (val seatType = coachViewModel.checkSeatType(seatDetail)) {
            VERTICAL_SLEEPER, SEATER, HORIZONTAL_SLEEPER, IMAGE_ICON -> {
                val resourceId = when (seatType) {
                    VERTICAL_SLEEPER -> getVerticalSeatIcon(seatDetail)
                    SEATER -> getSeaterIcon(seatDetail)
                    HORIZONTAL_SLEEPER -> getHorizontalSeatIcon(seatDetail)
                    IMAGE_ICON -> getImageIcon(seatDetail)
                    else -> 0
                }
                imageView?.setImageResource(resourceId)
            }

            else -> imageView?.visibility = View.INVISIBLE
        }
        if (fragmentPosition == FRAGMENT_RIGHT) {
            if (seatDetail.available == true) {
                seatDetail.backgroundColor = "#FFFFFF"
            } else {
                //seatDetail.backgroundColor = "#DFDCDC"
            }
        }

        if (isSelected) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        val tintColor = if (isSelected) selectedSeatColor else seatDetail.backgroundColor

        if(fragmentPosition == FRAGMENT_LEFT) {
            tintColor?.let {
                imageView?.setColorFilter(Color.parseColor(it))
            } ?: imageView?.setColorFilter(Color.parseColor("#DFDCDC"))
        }else
        {
            if (isSelected)
                imageView?.setColorFilter(Color.parseColor(tintColor))
            else
                imageView?.setColorFilter(Color.parseColor("#DFDCDC"))
        }


    }


    private fun drawCoach(
        seatDetail: SeatDetail,
        gridLayout: CustomGridLayout,
        gridLayoutUpper: CustomGridLayout,
        frameLayout: FrameLayout,
        imageView: ImageView,
        textView: TextView
    ) {
        val seatType = coachViewModel.checkSeatType(seatDetail)
        val iconResourceId = when (seatType) {
            VERTICAL_SLEEPER -> getVerticalSeatIcon(seatDetail)
            SEATER -> getSeaterIcon(seatDetail)
            HORIZONTAL_SLEEPER -> getHorizontalSeatIcon(seatDetail)
            IMAGE_ICON -> getImageIcon(seatDetail)
            else -> {
                gridLayoutUpper.visibility = View.INVISIBLE
                getSeaterIcon(seatDetail)
            }
        }
        setCoach(
            seatDetail,
            iconResourceId,
            gridLayout,
            gridLayoutUpper,
            getRowSpan(seatType),
            getColumnSpan(seatType),
            frameLayout,
            imageView,
            textView
        )
    }

    private fun getRowSpan(seatType: String): Int {
        return when (seatType) {
            VERTICAL_SLEEPER, SEATER, IMAGE_ICON -> 2
            else -> 1
        }
    }

    private fun getColumnSpan(seatType: String): Int {
        return when (seatType) {
            HORIZONTAL_SLEEPER -> 2
            else -> 1
        }
    }

    private fun getHorizontalSeatIcon(seatDetail: SeatDetail): Int {
        return if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.passengerDetails?.isHisBooking == true && seatDetail.available == true) R.drawable.agent_outline_horizontal
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.available == true && !seatDetail.isSelected) R.drawable.ic_dev_available_horizontal
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.passengerDetails?.isHisBooking == true && seatDetail.isBlocked == true) R.drawable.agent_outline_lock_horizontal
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.isBlocked == true) R.drawable.ic_dev_lock_outline_horizontal
        else if (seatDetail.passengerDetails?.isHisBooking == true && seatDetail.isBlocked == true) R.drawable.agent_birth_horizontal_lock
        else if (seatDetail.available == false && seatDetail.isBlocked == true) R.drawable.ic_dev_lock_horizontal
        else R.drawable.ic_dev_selected_horizontal
    }


    private fun getSeaterIcon(seatDetail: SeatDetail): Int {
        return if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.passengerDetails?.isHisBooking == true && seatDetail.available == true) R.drawable.agent_outline_seat
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.available == true && !seatDetail.isSelected) R.drawable.ic_dev_available_seat
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.passengerDetails?.isHisBooking == true && seatDetail.isBlocked == true) R.drawable.agent_outline_lock_seat
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.isBlocked == true) R.drawable.ic_dev_lock_outline_seat
        else if (seatDetail.passengerDetails?.isHisBooking == true && seatDetail.isBlocked == true) R.drawable.agent_lock_seat/*else if (seatDetail.available == false && seatDetail.isBlocked == true)
            R.drawable.ic_dev_lock_seat*/
        else R.drawable.ic_dev_selected_seats
    }

    private fun getVerticalSeatIcon(seatDetail: SeatDetail): Int {
        return if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.passengerDetails?.isHisBooking == true && seatDetail.available == true) R.drawable.agent_outline_vertical
        else if ((seatDetail.backgroundColor.isNullOrEmpty() || seatDetail.backgroundColor == WHITE_COLOR) && seatDetail.available == true && !seatDetail.isSelected) R.drawable.ic_dev_available_vertical
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.passengerDetails?.isHisBooking == true && seatDetail.isBlocked == true) R.drawable.agent_outline_lock_vertical
        else if (seatDetail.backgroundColor == WHITE_COLOR && seatDetail.isBlocked == true) R.drawable.ic_dev_lock_outline_vertical
        else if (seatDetail.passengerDetails?.isHisBooking == true && seatDetail.isBlocked == true) R.drawable.agent_birth_verticl_lock
        else if (seatDetail.available == false && seatDetail.isBlocked == true) R.drawable.ic_dev_lock_vertical
        else R.drawable.ic_dev_selected_vertical
    }

    private fun getImageIcon(seatDetail: SeatDetail): Int {
        return when (seatDetail.number) {
            DR_IMG -> {
                R.drawable.ic_driver
            }

            TV_IMG -> {
                //R.drawable.television
                R.drawable.ic_driver
            }

            PA_IMG -> {
                //R.drawable.restaurant
                R.drawable.ic_driver
            }

            WR_IMG -> {
                //R.drawable.wash_room
                R.drawable.ic_driver
            }

            SM_IMG -> {
                //R.drawable.smoking_area
                R.drawable.ic_driver
            }

            ST_IMG -> {
                //R.drawable.stair
                R.drawable.ic_driver
            }

            else -> R.drawable.ic_driver
        }
    }

    private fun seatClickListenerNew(fLayout: FrameLayout?, i: Int) {
        fLayout?.setOnClickListener {


            when (fragmentPosition) {

                FRAGMENT_LEFT -> {
                    if (seatDetails[i].passengerDetails?.ticketNo.isNullOrEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.this_seat_cannot_be_selected),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@setOnClickListener
                    }
                }

                FRAGMENT_RIGHT -> {
                    if (seatDetails[i].passengerDetails?.ticketNo.isNullOrEmpty().not()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.this_seat_cannot_be_selected),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@setOnClickListener
                    }
                }
            }

            onSeatSelectListener.onSeatClick(
                seatDetails[i].passengerDetails?.ticketNo ?: "", seatDetails[i], fragmentPosition
            )
        }
    }

    private fun setCoach(
        seatDetail: SeatDetail,
        seatIcon: Int,
        gridLayout: CustomGridLayout,
        gridLayoutUpper: CustomGridLayout,
        rowSpec: Int,
        columnSpec: Int,
        frameLayout: FrameLayout,
        imageView: ImageView,
        textView: TextView

    ) {
        imageView.setImageResource(seatIcon)
        val isLadiesSeat = seatDetail.isLadiesSeat ?: false
        val isShifted = seatDetail.isShifted ?: false
        val isUpdated = seatDetail.isUpdated ?: false
        val isInJourney = seatDetail.isInJourney ?: false
        val isMultiHop = seatDetail.isMultiHop ?: false
        val isGangway = seatDetail.isGangway ?: false
        val type = seatDetail.type ?: ""
        val isImage = seatDetail.number?.contains("IMG", true) == true

        val seatFare =
            if (serviceDetailsModel?.showFareOnSeat == true && !isGangway) "${serviceDetailsModel?.currency} ${seatDetail.fare ?: ""}\n"
            else ""
        val seatNumber = if (isImage) ""
        else seatDetail.number

        val seatText =
            "$seatNumber\n$seatFare${if (isLadiesSeat) "(F)" else ""}${if (isShifted) "S" else ""}${if (isUpdated) "*" else ""}${if (isInJourney) "#" else ""}${if (isMultiHop) "+" else ""}"
        textView.text = seatText
        unSelectedTintColor(imageView, textView, seatDetail)
        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(seatDetail.rowId!! - 1, rowSpec)
        params.columnSpec = GridLayout.spec(seatDetail.colId!! - 1, columnSpec)
        frameLayout.layoutParams = params

        if (isGangway) {
            frameLayout.layoutParams =
                FrameLayout.LayoutParams(25, frameLayout.layoutParams?.height ?: 0)
        }
        if (type.lowercase(Locale.getDefault())
                .contains("upper") || type.lowercase(Locale.getDefault()) == "ub" || (!seatDetail.floorType.isNullOrBlank() && seatDetail.floorType?.contains(
                "2"
            ) == true)
        ) {
            showUpperCoachTypeButton = true
            gridLayoutUpper.addView(frameLayout)
            seatDetailsMap[seatDetail] = frameLayout
        } else {
            seatDetailsMap[seatDetail] = frameLayout
            gridLayout.addView(frameLayout)
        }
    }

    private fun onUpperTabSelection() {
        binding.rbLower.apply {
            setTextColor(ContextCompat.getColor(context, R.color.gray_shade_a))
        }
        binding.rbUpper.apply {
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        binding.gridLayout.visibility = View.GONE
        binding.gridLayoutUpper.visibility = View.VISIBLE


        currentVisibleCoachType = Const.UPPER_COACH

        setBorderAroundSelectedPNRGroup(
            pnr = coachViewModel.currentPNR.value,
            seatDetail = coachViewModel.currentSeatDetail.value!!
        )
    }

    private fun onLowerTabSelection() {

        binding.rbLower.apply {
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        binding.rbUpper.apply {
            setTextColor(ContextCompat.getColor(context, R.color.gray_shade_a))
        }

        binding.gridLayout.visibility = View.VISIBLE
        binding.gridLayoutUpper.visibility = View.GONE

        currentVisibleCoachType = Const.LOWER_COACH

        setBorderAroundSelectedPNRGroup(
            pnr = coachViewModel.currentPNR.value,
            seatDetail = coachViewModel.currentSeatDetail.value!!
        )

    }

    private fun startAnimation(imageView: ImageView?) {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000 //You can manage the blinking time with this parameter

        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        imageView?.startAnimation(anim)
    }

    fun setRecommendedSeats(seatNumbers: List<String?>?) {
        seatDetailsMap.forEach {
            seatNumbers?.forEach { seatNumber ->
                if (it.key.number.equals(seatNumber)) {

                    val viewGroup = it.value as FrameLayout
                    startAnimation(viewGroup.findViewById(R.id.imageView))
                }
            }
        }
    }

    private fun setCoachHeader() {
        when (fragmentPosition) {

            FRAGMENT_LEFT -> {

                val coachHeaderTitle = SpannableStringBuilder()
                    .underline {
                        append(getString(R.string.from) + " ")
                    }
                    .underline {
                        bold { append(serviceDetailsModel?.body?.number.toString()) }
                    }


                binding.tvCoachHeader.setText(coachHeaderTitle, TextView.BufferType.SPANNABLE)

            }

            FRAGMENT_RIGHT -> {

                val coachHeaderTitle = SpannableStringBuilder()
                    .underline {
                        append(getString(R.string.to) + " ")
                    }
                    .underline {
                        bold { append(serviceDetailsModel?.body?.number ?: "") }
                    }

                binding.tvCoachHeader.setText(coachHeaderTitle, TextView.BufferType.SPANNABLE)

            }

        }

        binding.tvCoachHeader.setOnClickListener {
            onSeatSelectListener.onServiceDetailsClicked(fragmentPosition,serviceDetailsModel)
        }
    }

    fun selectSeat(seatDetail: SeatDetail) {
        var mapItem: Map.Entry<SeatDetail, View>? = null
        Log.d(
            TAG,
            "selectSeat: Seat Number from Param: ${seatDetail.number}, HasMapSize: ${seatDetailsMap.size}"
        )
        seatDetailsMap.forEach {
            if (it.key.number.equals(seatDetail.number)) {
                it.key.isSelected = true
                mapItem = it
                //coachViewModel.currentPNR.value = seatDetail.passengerDetails?.ticketNo
            }

            Log.d(TAG, "seatDetailsMap: ${it.key.number}")
        }
        if (mapItem != null) {
            val viewGroup = mapItem!!.value as FrameLayout
            val textView: TextView = viewGroup.findViewById(R.id.tvTop)
            val imageView: ImageView = viewGroup.findViewById(R.id.imageView)

            updateSelectedSeatsListAndUI(mapItem?.key!!, imageView, textView)
        } else {
            Log.d(TAG, "updateSelectedSeatsListAndUI: Blablabla --> ${seatDetail.number}")
        }

    }

    private fun updateSelectedSeatsListAndUI(
        seatDetail: SeatDetail,
        imageView: ImageView?,
        textView: TextView
    ) {
        //if (!coachViewModel.selectedSeats.contains(seatDetail.number)) {
            if (serviceDetailsModel?.setMaxSeatSelection == 0 || coachViewModel.selectedSeats.size < (serviceDetailsModel?.setMaxSeatSelection
                    ?: 0)
            ) {
                if (!coachViewModel.selectedSeats.contains(seatDetail.number)) {
                    coachViewModel.selectedSeats.add(seatDetail.number ?: "")
                }
                updateTintColorNew(seatDetail, imageView, textView, true)

                coachViewModel.currentPNR.value = seatDetail.passengerDetails?.ticketNo ?: ""
                coachViewModel.currentSeatDetail.value = seatDetail

                /*if (binding.gridLayout.isVisible) {
                    currentVisibleCoachType = Const.LOWER_COACH

                    setBorderAroundSelectedPNRGroup(
                        pnr = coachViewModel.currentPNR.value,
                        seatDetail = seatDetail
                    )
                } else {
                    currentVisibleCoachType = Const.UPPER_COACH

                    setBorderAroundSelectedPNRGroup(
                        pnr = coachViewModel.currentPNR.value,
                        seatDetail = seatDetail
                    )
                }*/
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(
                        R.string.you_can_t_select_more_than,
                        serviceDetailsModel?.setMaxSeatSelection.toString()
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        //}
    }

    fun deSelectSeat(seatDetail: SeatDetail) {
        var mapItem: Map.Entry<SeatDetail, View>? = null

        Log.d(
            TAG,
            "selectSeat: Seat Number from Param: ${seatDetail.number}, HasMapSize: ${seatDetailsMap.size}"
        )

        seatDetailsMap.forEach {
            if (it.key.number.equals(seatDetail.number)) {
                it.key.isSelected = false
                mapItem = it
            }
        }

        if (mapItem != null) {
            val viewGroup = mapItem!!.value as FrameLayout
            val textView: TextView = viewGroup.findViewById(R.id.tvTop)
            val imageView: ImageView = viewGroup.findViewById(R.id.imageView)

            coachViewModel.selectedSeats.remove(seatDetail.number)
            updateTintColorNew(mapItem?.key!!, imageView, textView, false)
        }
    }


    fun clearAnimationForAllSeats() {
        seatDetailsMap.forEach {
            val viewGroup = it.value as FrameLayout
            val imageView: ImageView = viewGroup.findViewById(R.id.imageView)

            imageView.clearAnimation()
        }
    }

    fun setBorderAroundSelectedPNRGroup(pnr: String?, seatDetail: SeatDetail) {
        if (pnr != null && fragmentPosition == FRAGMENT_LEFT) {
            coachViewModel.currentPNR.value = pnr
            coachViewModel.currentSeatDetail.value = seatDetail

            when (currentVisibleCoachType) {
                Const.LOWER_COACH -> {
                    binding.gridLayout.drawBorder(
                        coachType = Const.LOWER_COACH,
                        pnr = coachViewModel.currentPNR.value,
                        seatDetailsMap = seatDetailsMap,
                        isDrawBorder = true,
                        seatDetail = coachViewModel.currentSeatDetail.value

                    )
                }

                Const.UPPER_COACH -> {
                    binding.gridLayoutUpper.drawBorder(
                        coachType = Const.UPPER_COACH,
                        pnr = coachViewModel.currentPNR.value,
                        seatDetailsMap = seatDetailsMap,
                        isDrawBorder = true,
                        seatDetail = coachViewModel.currentSeatDetail.value
                    )
                }
            }
        } /* else {
            Toast.makeText(requireContext(), "PNR can't be null", Toast.LENGTH_SHORT).show()
        }*/
    }

    fun removeBorderAroundSelectedPNRGroup() {
        when (currentVisibleCoachType) {
            Const.LOWER_COACH -> {
                binding.gridLayout.drawBorder(
                    coachType = Const.LOWER_COACH,
                    pnr = "",
                    seatDetailsMap = seatDetailsMap,
                    isDrawBorder = false,
                    seatDetail = coachViewModel.currentSeatDetail.value
                )
            }

            Const.UPPER_COACH -> {
                binding.gridLayoutUpper.drawBorder(
                    coachType = Const.UPPER_COACH,
                    pnr = "",
                    seatDetailsMap = seatDetailsMap,
                    isDrawBorder = false,
                    seatDetail = coachViewModel.currentSeatDetail.value
                )
            }
        }

    }


    fun showToolTipForServiceName() {

        when (fragmentPosition) {

            FRAGMENT_LEFT -> {


//                val toolTipWindow = TooltipWindow(
//                    ctx = requireContext(),
//                    header = "Shift seats ${binding.tvCoachHeader.text}",
//                    description = getString(R.string.tap_a_seat_icon_to_select_seat_from_this_service),
//                    buttonLabel = "Next"
//                ) {
//                    onSeatSelectListener.onToolTipForServiceNameClick(fragmentPosition)
//                }
//                toolTipWindow.showToolTip(binding.tvCoachHeader)
                /*Thread.sleep(5000)
                toolTipWindow.dismissTooltip()*/
            }

            FRAGMENT_RIGHT -> {
                val toolTipWindow = TooltipWindow(
                    ctx = requireContext(),
                    header = getString(R.string.shift_seats, binding.tvCoachHeader.text),
                    description = getString(R.string.tap_a_seat_icon_to_assign_the_seat),
                    buttonLabel = getString(R.string.finish)
                ) {

                }
                toolTipWindow.showToolTip(binding.tvCoachHeader)
                /*Thread.sleep(5000)
                toolTipWindow.dismissTooltip()*/
            }

        }
    }

    private fun setNoBerthTextToUpperRadioGroup() {
        binding.rbUpper.text = getString(R.string.no_berth)
        binding.rbUpper.isClickable = false
    }

    fun setMultiHopAdapter(multiHopSeatDetailsList: MutableList<MultiHopSeatDetail>) {

        if(multiHopSeatDetailsList.isNotEmpty()) {
            val seatDetail = multiHopSeatDetailsList.get(0).seat_details?.get(0)!!
            val seatType = coachViewModel.checkSeatType(seatDetail)
            val iconResourceId = when (seatType) {
                VERTICAL_SLEEPER -> getVerticalSeatIcon(seatDetail)
                SEATER -> getSeaterIcon(seatDetail)
                HORIZONTAL_SLEEPER -> getHorizontalSeatIcon(seatDetail)
                IMAGE_ICON -> getImageIcon(seatDetail)
                else -> {
                    getSeaterIcon(seatDetail)
                }
            }

            binding.radioGroup.visibility = View.GONE
            binding.gridLayoutParent.visibility = View.GONE
            binding.rvMergeBus.visibility = View.VISIBLE

            multiHopSeatSamePnrGroupAdapter =
                MultiHopSeatSamePnrGroupParentAdapter(context = requireContext(),
                    iconResourceId = iconResourceId,
                    multiHopSeatDetailsList = multiHopSeatDetailsList,
                    setAdapterCallback = { position, item ->
                        multiHopChildAdapterMap[position] = item
                    },
                    onClickParentAdapter = { parentPosition, childPosition, childSeatDetail ->
                        onSeatSelectListener.onMultiHopSeatClickNew(
                            parentPosition,
                            childPosition,
                            childSeatDetail
                        )
                        //Toast.makeText(requireContext(), it.ticketNo, Toast.LENGTH_SHORT).show()
                    }
                )
            binding.rvMergeBus.adapter = multiHopSeatSamePnrGroupAdapter
        }
    }

    fun selectSpecificSeatInMultiHopAdapter(
        parentPosition: Int,
        childPosition: Int,
        seatDetail: SeatDetail
    ) {
        if (::multiHopSeatSamePnrGroupAdapter.isInitialized) {
            //multiHopSeatSamePnrGroupAdapter.updateData(2)
            multiHopChildAdapterMap.get(parentPosition)?.updateItemAt(childPosition, seatDetail)
        }
    }

    fun selectOrDeselectAllSeatsOfSpecificPNRGroupInMultiHopAdapter(
        parentPosition: Int,
        seatDetailList: MutableList<SeatDetail>
    ) {
        if (::multiHopSeatSamePnrGroupAdapter.isInitialized) {
            //multiHopSeatSamePnrGroupAdapter.updateData(2)
            multiHopSeatSamePnrGroupAdapter.drawBorderAroundPNRGroupAt(parentPosition)
            multiHopChildAdapterMap.get(parentPosition)?.updateList(seatDetailList)
        }
    }

    fun onMultiHopDoneClick() {
        onSeatSelectListener.onDoneButtonClick()
    }

    fun onMultiHopOnCancelClick() {

        onSeatSelectListener.onCancelButtonClick()

    }

    fun hideMultiHopLayout() {
        binding.radioGroup.visibility = View.VISIBLE
        binding.gridLayoutParent.visibility = View.VISIBLE
        binding.rvMergeBus.visibility = View.GONE
    }

    fun isMultiHopLayoutVisible(): Boolean {
        return binding.rvMergeBus.isVisible
    }
    fun toggleDoneAndGoBackButtonVisibility(showDoneButton: Boolean) {

        if (showDoneButton) {
            binding.llDone.visibility = View.VISIBLE
            binding.llGoBack.visibility = View.GONE
        } else {
            binding.llDone.visibility = View.GONE
            binding.llGoBack.visibility = View.VISIBLE
        }
    }

    private fun selectPreviouslySelectedSeats() {
        onSeatSelectListener.selectPreviouslySelectedSeats(
            pnr = coachViewModel.currentPNR.value ?: "",
            fragmentPosition = FRAGMENT_RIGHT
        )
    }
}