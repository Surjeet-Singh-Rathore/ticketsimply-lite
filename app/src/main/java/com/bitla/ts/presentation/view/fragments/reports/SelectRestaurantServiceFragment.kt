package com.bitla.ts.presentation.view.fragments.reports

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bitla.ts.R
import com.bitla.ts.databinding.FragmentSelectRestaurantServiceBinding
import com.bitla.ts.domain.pojo.mealCoupon.RestaurantList
import com.bitla.ts.domain.pojo.mealCoupon.RestaurantListResponse
import com.bitla.ts.presentation.adapter.RestaurantListAdapter
import com.bitla.ts.presentation.viewModel.RestaurantViewModel
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast

import java.util.Locale

class SelectRestaurantServiceFragment : Fragment() {

    private lateinit var restaurantAdapter: RestaurantListAdapter
    private var binding: FragmentSelectRestaurantServiceBinding? = null
    private val viewModel by viewModel<RestaurantViewModel>()
    private var filteredRestaurantList = mutableListOf<RestaurantList>()
    private var restaurantListResponse = RestaurantListResponse()
    private var apiKey = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectRestaurantServiceBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       arguments?.let {
           apiKey=it.getString("apiKey").toString()
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callRestaurantListApi()


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



    private fun search(text: String) {

            filteredRestaurantList = mutableListOf()
            for (s in restaurantListResponse.restaurantList!!) {
                if (s.id != null && s.restaurantName?.lowercase(Locale.getDefault())?.contains(
                        text.lowercase(
                            Locale.getDefault()
                        )
                    ) == true
                ) {
                    filteredRestaurantList.add(s)
                }
            }
            if (::restaurantAdapter.isInitialized) {
                restaurantAdapter.addData(filteredRestaurantList)
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
            "${data}"
        )
        findNavController().popBackStack()
    }


}