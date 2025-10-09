package com.bitla.restaurant_app.presentation.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.databinding.FragmentSelectRestaurantServiceBinding
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.RestaurantList
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.RestaurantListResponse
import com.bitla.restaurant_app.presentation.utils.PreferenceUtils
import com.bitla.restaurant_app.presentation.utils.getDateYMD
import com.bitla.restaurant_app.presentation.utils.getTodayDate
import com.bitla.restaurant_app.presentation.utils.toast
import com.bitla.restaurant_app.presentation.view.MainActivity
import com.bitla.restaurant_app.presentation.view.adapters.RestaurantListAdapter
import com.bitla.restaurant_app.presentation.view.adapters.ServicesListAdapter
import com.bitla.restaurant_app.presentation.viewModel.RestaurantViewModel
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctRequest.AllotedDirectRequest
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse.AllotedDirectResponse
import com.bitla.restaurant_app.presentation.utils.isNetworkAvailable
import java.util.Locale

class SelectRestaurantServiceFragment : Fragment() {

    private lateinit var restaurantAdapter: RestaurantListAdapter
    private lateinit var servicesAdapter: ServicesListAdapter
    private var binding: FragmentSelectRestaurantServiceBinding? = null
    private val args: SelectRestaurantServiceFragmentArgs by navArgs()
    private val viewModel by viewModels<RestaurantViewModel>()
    private var filteredRestaurantList = mutableListOf<RestaurantList>()
    private var filteredServicesList = mutableListOf<Service>()
    private var restaurantListResponse = RestaurantListResponse()
    private var allotedServicesDirectResponse: AllotedDirectResponse? = null
    private var apiKey = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectRestaurantServiceBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).hideToolBar()
        getApiKey()
        val isRestaurant = args.isRestaurant


        if (isRestaurant) {
            callRestaurantListApi()
        } else {
            callAllotedServiceDirectApi()
        }


        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                search(s.toString())
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

        setObservers()

        binding?.toolbarImageLeft?.setOnClickListener {
            navigateBack()
        }

    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun callRestaurantListApi() {
        if (requireContext().isNetworkAvailable()) {
            viewModel.getRestaurantListApi(apiKey)
        } else {
            requireContext().toast(getString(R.string.network_not_available))
        }
    }

    private fun callAllotedServiceDirectApi() {

        if (requireContext().isNetworkAvailable()) {
            viewModel.allotedServiceApiDirect(

                AllotedDirectRequest(
                    is_group_by_hubs = false,
                    hub_id = null,
                    api_key = apiKey,
                    travel_date = getDateYMD(getTodayDate()),
                    page = null,
                    per_page = null,
                    view_mode = "report",
                    pagination = false,
                    origin = null,
                    destination = null,
                    locale = "id",
                    isCheckingInspector = null,
                    serviceFilter = null
                )
            )
        } else {
            requireContext().toast(getString(R.string.network_not_available))
        }
    }

    private fun search(text: String) {
        if (args.isRestaurant) {
            filteredRestaurantList = mutableListOf()
            for (s in restaurantListResponse.restaurantList ?: arrayListOf()) {
                if (s.id != null && s.restaurantName!!.lowercase(Locale.getDefault()).contains(
                        text.lowercase(
                            Locale.getDefault()
                        )
                    )
                ) {
                    filteredRestaurantList.add(s)
                }
            }
            if (::restaurantAdapter.isInitialized) {
                restaurantAdapter.addData(filteredRestaurantList)
            }
        } else {
            if (allotedServicesDirectResponse != null) {
                filteredServicesList = mutableListOf()
                for (s in allotedServicesDirectResponse?.services!!) {
                    if (s.routeId != null && s.number.lowercase(Locale.getDefault()).contains(
                            text.lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) {
                        filteredServicesList.add(s)
                    }
                }
                if (::servicesAdapter.isInitialized) {
                    servicesAdapter.addData(filteredServicesList)
                }
            }
        }

    }

    private fun setObservers() {

        viewModel.restaurantListResponse.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.code == 200) {

                    val restaurantData = RestaurantList()
                    restaurantData.id = -1
                    restaurantData.restaurantName = getString(R.string.all_restaurants)
                    it.restaurantList?.add(0, restaurantData)

                    restaurantListResponse = it
                    if (it.restaurantList?.size!! > 0) {
                        binding?.progressBarList?.visibility = View.GONE
                        setRestaurantListAdapter(it.restaurantList!!)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))

            }
        })




        viewModel.dataAllotedServiceDirect.observe(this) { it ->
            if (it != null) {
                when (it.code) {
                    200 -> {
                        val serviceData = Service()
                        serviceData.routeId = -1
                        serviceData.number = getString(R.string.all_services)
                        it.services?.add(0, serviceData)
                        allotedServicesDirectResponse = it
                        if (it.services?.isEmpty() == true) {
                            binding?.progressBarList?.visibility = View.GONE
                            binding?.NoResult?.visibility = View.GONE
                            binding?.noResultText?.text = getString(R.string.no_data_available)
                        } else {
                            binding?.progressBarList?.visibility = View.GONE
                            setServicesAdapter(it.services!!)
                        }
                    }

                    401 -> {

                    }

                    else -> {
                        if (it.result?.message != null) {
                            binding?.progressBarList?.visibility = View.GONE
                            binding?.NoResult?.visibility = View.VISIBLE
                            binding?.noResultText?.text = it.result.message.let { it ->
                                getString(R.string.no_data_available)
                            }
                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }

        }
    }

    private fun setServicesAdapter(services: ArrayList<Service>) {
        servicesAdapter =
            ServicesListAdapter(requireContext()) { position -> setClickedResult(position) }
        servicesAdapter.addData(services)
        binding?.listRv?.adapter = servicesAdapter
    }

    private fun setRestaurantListAdapter(restaurantList: ArrayList<RestaurantList>) {
        restaurantAdapter =
            RestaurantListAdapter(requireContext()) { position -> setClickedResult(position) }
        restaurantAdapter.addData(restaurantList)
        binding?.listRv?.adapter = restaurantAdapter
    }

    private fun setClickedResult(data: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "selectedResult",
            data
        )
        findNavController().popBackStack()
    }


    private fun getApiKey() {
        apiKey = PreferenceUtils.getLogin().api_key
    }
}