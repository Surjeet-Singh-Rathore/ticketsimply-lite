package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.view.*
import android.view.animation.*
import android.view.animation.Interpolator
import android.widget.*
import androidx.annotation.*
import androidx.lifecycle.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.*
import gone
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible
import java.time.*
import java.time.format.*
import java.time.temporal.*



class BusTrackingFragment : BaseFragment(), OnMapReadyCallback {
    companion object {
        val tag: String = BusTrackingFragment::class.java.simpleName
    }

    private var tvCurrentCity: TextView? = null
    private var tvCoachNumber: TextView? = null
    private var tvServiceNumber: TextView? = null
    private val sheetHeight: Int = 400
    private val cameraZoom: Float = 15f
    lateinit var binding: FragmentBusTrackingBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var marker: Marker
    private val busTrackingViewModel by sharedViewModel<BusTrackingViewModel<Any?>>()
    private var isMarkerInitialised = false
    private var loginModelPref: LoginModel = LoginModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusTrackingBinding.inflate(inflater, container, false)

        return binding.root
    }

    fun isWithinNext24Hours(dateTimeString: String, format: String): Boolean {
        // Define the date-time formatter
        try {
            if(!dateTimeString.isNullOrEmpty()){
                val formatter = DateTimeFormatter.ofPattern(format)

                // Parse the input date-time string
                val inputDateTime = LocalDateTime.parse(dateTimeString, formatter)

                // Get the current date-time
                val currentDateTime = LocalDateTime.now()

                // Calculate the date-time 24 hours from now
                val next24Hours = currentDateTime.plus(24, ChronoUnit.HOURS)

                // Check if the input date-time is within the next 24 hours
                return (inputDateTime.isAfter(currentDateTime) && inputDateTime.isBefore(next24Hours)) || inputDateTime.isBefore(
                    currentDateTime.plusSeconds(1)
                )
            }else{
                return false
            }

        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
            return false
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()

        val outputFormat = if((busTrackingViewModel.deptDateTime.value?.contains("AM",true) == true) || (busTrackingViewModel.deptDateTime.value?.contains("PM",true) == true)) DATE_FORMAT_HH_MM_12_24_DD_MM_YYYY else DATE_FORMAT_HH_MM__DD_MM_YYYY

        val date = inputFormatToHMTDMY(
            busTrackingViewModel.deptDateTime.value?.toString()?:"",
            outputFormat
        )


        if (!isWithinNext24Hours(date.toString(), DATE_FORMAT_YMDTHMS)) {
            if (busTrackingViewModel.coachNumber.value?.trim()?.isEmpty()==true || busTrackingViewModel.coachNumber.value=="null") {
                binding.bottomSheet.gone()
                binding.NoResult.visible()
                binding.messageTV.text =
                    requireContext().getString(R.string.vehicle_is_not_assigned)

                if ((activity as BaseActivity).getPrivilegeBase()?.allowUpdateDetailsOptionInReservationChart == true) {
                    binding.addBusDetailsButton.visible()
                } else {
                    binding.addBusDetailsButton.gone()
                }
            } else {
                binding.bottomSheet.gone()
                binding.NoResult.visible()
                binding.messageTV.text =
                    getString(R.string.tracking_will_be_available_before_24_hrs_of_departure_time)
                binding.addBusDetailsButton.gone()
            }
        } else {
            if (busTrackingViewModel.coachNumber.value?.trim()?.isEmpty()==true || busTrackingViewModel.coachNumber.value=="null") {
                binding.bottomSheet.gone()
                binding.NoResult.visible()
                binding.messageTV.text =
                    requireContext().getString(R.string.vehicle_is_not_assigned)

                if ((activity as BaseActivity).getPrivilegeBase()?.allowUpdateDetailsOptionInReservationChart == true) {
                    binding.addBusDetailsButton.visible()
                } else {
                    binding.addBusDetailsButton.gone()
                }
            } else {
                getPref()
                initUI()
                addBottomSheet()
                setupObserver()
            }
        }


    }

    private fun getPref() {
        loginModelPref = getLogin()


    }

    private fun setClickListener() {
        binding.addBusDetailsButton.setOnClickListener {
            firebaseLogEvent(
                requireContext(),
                BOOKINGPG_UPDATE_DETAILS,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                BOOKINGPG_UPDATE_DETAILS,
                "Update Details"
            )
            val busDetails =
                "${busTrackingViewModel.serviceNumber.value} | ${getDateDMYY(busTrackingViewModel.serviceTravelDate.value.toString())} ${busTrackingViewModel.deptTime.value} | ${busTrackingViewModel.serviceBusType.value}"
            val intent = Intent(requireContext(), ServiceDetailsActivity::class.java)
            intent.putExtra(
                requireContext().getString(R.string.origin),
                busTrackingViewModel.origin.value
            )
            intent.putExtra(
                requireContext().getString(R.string.destination),
                busTrackingViewModel.destination.value
            )
            intent.putExtra(requireContext().getString(R.string.bus_type), busDetails)

            PreferenceUtils.removeKey(requireContext().getString(R.string.scannedUserName))
            PreferenceUtils.removeKey(requireContext().getString(R.string.scannedUserId))
            PreferenceUtils.removeKey("selectedScanType")
            PreferenceUtils.removeKey(requireContext().getString(R.string.scan_coach))
            PreferenceUtils.removeKey(requireContext().getString(R.string.scan_driver_1))
            PreferenceUtils.removeKey(requireContext().getString(R.string.scan_driver_2))
            PreferenceUtils.removeKey(requireContext().getString(R.string.scan_cleaner))
            PreferenceUtils.removeKey(requireContext().getString(R.string.scan_contractor))

            requireActivity().finish()
            context?.startActivity(intent)


        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupObserver() {
        busTrackingViewModel.busLocation.observe(viewLifecycleOwner, Observer { location ->
            location?.let {
                if (it.success == 0) {
                    busTrackingViewModel.job?.cancel()
                    if (it.message.contains(requireContext().getString(R.string.vehicle_is_not_assigned))) {
                        binding.bottomSheet.gone()
                        binding.NoResult.visible()
                        binding.messageTV.text =
                            getString(R.string.bus_is_assigned_but_gps_is_not_available)
                        binding.addBusDetailsButton.gone()
                    }
                } else {
                    val locationList = busTrackingViewModel.locations.value!!
                    val currentLocation = busTrackingViewModel.currentLocationIndex.value
                    if (currentLocation == 0 || (it.data.lat_long != "${locationList[currentLocation!!].latitude},${locationList[currentLocation!!].longitude}")) {

                        busTrackingViewModel.locations.value?.add(
                            LatLng(
                                it.data?.lat?.toDouble() ?: 0.0, it.data?.long?.toDouble() ?: 0.0
                            )
                        )
                        busTrackingViewModel.updateLocation()
                        if (!isMarkerInitialised)
                            initMarkerObserver(googleMap)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.data?.lat?.toDouble() ?: 0.0,
                                    it.data?.long?.toDouble() ?: 0.0
                                ), cameraZoom
                            )
                        )
                        updateMarkerPosition()
                    }
                }
            }


        })

        busTrackingViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            // Show error message
            context?.toast(message)
        })
    }


    private fun initUI() {


        addMap()
        addBottomSheet()
    }

    private fun addBottomSheet() {
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = sheetHeight
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        with(binding.bottomSheet) {
            tvCurrentCity = findViewById(R.id.tvCurrentCity)
            tvCoachNumber = findViewById(R.id.tvCoachNumber)
            tvServiceNumber = findViewById(R.id.tvServiceNumber)
        }

        setBottomSheetText()
    }

    private fun setBottomSheetText() {
        val coachNumber = "${busTrackingViewModel.coachNumber.value}     ."
        if (!busTrackingViewModel.coachNumber.value.isNullOrEmpty() && busTrackingViewModel.coachNumber.value != "null")
            tvCoachNumber?.text = coachNumber
        tvServiceNumber?.text = busTrackingViewModel.serviceNumber.value
    }

    private fun addMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.busTrackingMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        busTrackingViewModel.updateMarkerLocation()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun updateMarkerPosition() {
        busTrackingViewModel.currentLocationIndex.observe(viewLifecycleOwner, Observer { index ->
            val locations = busTrackingViewModel.locations.value ?: return@Observer
            if (index < locations.size) {
                val startPos = marker.position
                val endPos = locations[index]
                animateTheBusBetweenTheLatLng(startPos, endPos)
//                CoroutineScope(Dispatchers.Main).launch {
//                    val cityName =
//                        getCityFromLocation(context = requireContext(), location = endPos)
//                    //  tvCurrentCity?.text = cityName
//
//                }
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endPos, cameraZoom))
            }
        })
    }

    private fun animateTheBusBetweenTheLatLng(
        startPosition: LatLng,
        endPosition: LatLng,
    ) {
        if (startPosition == endPosition) {
            return
        }


        val handler = Handler()
        val start: Long = SystemClock.uptimeMillis()
        val proj: Projection = googleMap.getProjection()
        val startPoint: Point = proj.toScreenLocation(marker.position)
        val startLatLng: LatLng = proj.fromScreenLocation(startPoint)
        val duration: Long = 2000

        val interpolator: Interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed: Long = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lng: Double = t * endPosition.longitude + (1 - t) * startLatLng.longitude
                val lat: Double = t * endPosition.latitude + (1 - t) * startLatLng.latitude
                marker.position = LatLng(lat, lng)
                marker.rotation = calculateBearing(startPosition, endPosition)

                if (t < 1.0) {
                    handler.postDelayed(this, 16)
                } else {
//                    if (hideMarker) {
//                        marker.isVisible = false
//                    } else {
//                        marker.isVisible = true
//                    }
                }
            }
        })


//        val bearing = calculateBearing(startPosition, endPosition)
//        val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
//        valueAnimator.duration = 3000
//        valueAnimator.interpolator = LinearInterpolator()
//        valueAnimator.addUpdateListener {
//            val v = valueAnimator.animatedFraction
//            val lng = v * endPosition.longitude + (1 - v) * startPosition.longitude
//            val lat = v * endPosition.latitude + (1 - v) * startPosition.latitude
//            val newPos = LatLng(lat, lng)
//            marker.position = newPos
//            //  marker.rotation = bearing
//            marker.setAnchor(0.5f, 0.5f)
//        }
//        valueAnimator.start()
    }

    private fun initMarkerObserver(map: GoogleMap) {
//        busTrackingViewModel.locations.observe(viewLifecycleOwner) { locations ->
        marker = map.addMarker(
            MarkerOptions().position(busTrackingViewModel.locations.value!![busTrackingViewModel.currentLocationIndex.value!!])
                .flat(true).icon(
                bitmapFromVector(
                    requireContext(),
                    R.drawable.bus_top_view
                )
            )
        )!!
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                busTrackingViewModel.locations.value!![busTrackingViewModel.currentLocationIndex.value!!],
                cameraZoom
            )
        )
        isMarkerInitialised = true
//        }
    }


    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }


}