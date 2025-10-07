package com.bitla.ts.presentation.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.DirectionsService
import com.bitla.ts.databinding.FragmentRouteReviewBinding
import com.bitla.ts.domain.pojo.direction_response.DirectionsResponse
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.preview_route.BoardingPointListLatLong
import com.bitla.ts.domain.pojo.preview_route.StageData
import com.bitla.ts.presentation.view.activity.RouteServiceManagerActivity
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import toast

class PreviewRouteFragment : Fragment(), OnMapReadyCallback {

    private val binding by lazy { FragmentRouteReviewBinding.inflate(layoutInflater) }
    private lateinit var mMap: GoogleMap
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private var routeId: String = ""
    private var boardingList: ArrayList<StageData> = arrayListOf()
    private var droppingList: ArrayList<StageData> = arrayListOf()
    private var latLngArray: ArrayList<BoardingPointListLatLong> = arrayListOf()
    private var latLngMiddleCities: ArrayList<LatLng> = arrayListOf()


    private val BASE_URL: String = "https://maps.googleapis.com/maps/api/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initUI()
        return binding.root
    }

    private fun initUI() {
        routeId = viewModel.routeId.value.toString().trim()
        getPref()
        previewRouteObserver()
    }

    override fun onResume() {
        super.onResume()
        try {
            val data = viewModel.getRouteData.value?.peekContent()?.result
            val subTitle = "${data?.schedule?.departureTime}, ${data?.basicDetails?.originName}- ${data?.basicDetails?.destinationName}"
            (activity as RouteServiceManagerActivity).updateToolbar("Route Review","",subTitle)

        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        callPreviewRouteApi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleMap = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        googleMap.getMapAsync(this)
    }

    private fun previewRouteObserver() {
        viewModel.getPreviewRouteResponse.observe(viewLifecycleOwner, Observer { response ->
            try {
                response?.let {
                    when (response.code) {
                        "200" -> {
                            boardingList = response.result?.boardingList ?: arrayListOf()
                            droppingList = response.result?.droppingList ?: arrayListOf()
                            updateLatLngArray()
                        }
                        "401" -> {
                            (requireActivity() as BaseActivity).showUnauthorisedDialog()
                        }
                        else -> {
                            requireActivity().toast(getString(R.string.server_error))
                        }
                    }
                } ?: run {
                    requireActivity().toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                Log.e("previewRouteObserver", "Exception: ${e.message}", e)
            }
        })
    }

    private fun updateLatLngArray() {
        latLngArray.clear()
        for(i in boardingList.indices){
            val bpdp = BoardingPointListLatLong().apply {
                name = boardingList[i].name
                latLong = LatLng(boardingList[i].lat.toDouble(),boardingList[i].long.toDouble())
            }
            latLngArray.add(bpdp)
        }

        for(i in droppingList.indices){
            val bpdp = BoardingPointListLatLong().apply {
                name = droppingList[i].name
                latLong = LatLng(droppingList[i].lat.toDouble(),droppingList[i].long.toDouble())
            }
            latLngArray.add(bpdp)
        }
    }

    private fun callPreviewRouteApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                loginModelPref?.let {
                    viewModel.previewRouteApi(it.api_key, locale, routeId)
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (latLngArray.isEmpty()) return

        val builder = LatLngBounds.Builder()
        latLngArray.forEachIndexed { index, bpdp ->
            addColoredMarker(bpdp.latLong, bpdp.name, index)
            builder.include(bpdp.latLong)
        }

        val bounds = builder.build()
        val padding = 100
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))

        latLngMiddleCities.clear()
        for (i in 1 until latLngArray.size - 1) {
            latLngMiddleCities.add(latLngArray[i].latLong)
        }

        getDirections(latLngArray.first().latLong, latLngArray.last().latLong, *latLngMiddleCities.toTypedArray())
    }

    private fun addColoredMarker(position: LatLng, title: String, colorIndex: Int) {
        val colors = arrayOf(
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_MAGENTA
        )

        mMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(colors[colorIndex % colors.size]))
        )
    }

    private fun getDirections(origin: LatLng, destination: LatLng, vararg waypoints: LatLng) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DirectionsService::class.java)

        val originStr = "${origin.latitude},${origin.longitude}"
        val destinationStr = "${destination.latitude},${destination.longitude}"
        val waypointsStr = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }

        val call = service.getDirections(
            originStr,
            destinationStr,
            "AIzaSyA4rDay-KbRtwxiP9o0xhUyrcPeBwTsOaA",
            waypointsStr
        )

        call?.enqueue(object : Callback<DirectionsResponse?> {
            override fun onResponse(call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val path = response.body()!!.path
                    drawPath(path)
                }
            }

            override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                Log.e("MapFragment", "Error fetching directions: ${t.message}", t)
            }
        })
    }

    private fun drawPath(path: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(path)
            .color(R.color.blue_notification_unblocking)
            .width(5f)
        mMap.addPolyline(polylineOptions)
    }
}
