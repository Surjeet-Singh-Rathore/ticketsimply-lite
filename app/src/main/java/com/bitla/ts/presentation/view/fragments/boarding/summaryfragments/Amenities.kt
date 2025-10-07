package com.bitla.ts.presentation.view.fragments.boarding.summaryfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.FragmentAmenitiesBinding
import com.bitla.ts.domain.pojo.available_routes.BusAmenity
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.presentation.adapter.AmenitiesAdapter
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.AMENITIES
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible


class Amenities : Fragment() {
    private lateinit var binding: FragmentAmenitiesBinding
    private var busAmenities = mutableListOf<BusAmenity>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var amenitiesAdapter: AmenitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAmenitiesBinding.inflate(inflater, container, false)
        getPref()
        firebaseLogEvent(
        requireContext(),
        AMENITIES,
            PreferenceUtils.getLogin().userName,
            PreferenceUtils.getLogin().travels_name,
            PreferenceUtils.getLogin().role,
        AMENITIES,
        "Amenities"
        )
        if (busAmenities.isNotEmpty()) {
            binding.svAmenities.visible()
            binding.layoutNoData.root.gone()
            setAmenitiesAdapter()
        } else {
            binding.svAmenities.gone()
            binding.layoutNoData.root.visible()
        }
        return binding.root
    }

    private fun getPref() {
        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            busAmenities = result?.bus_amenities ?: busAmenities
        }
    }


    private fun setAmenitiesAdapter() {
        layoutManager = GridLayoutManager(context, 2)
        binding.rvamenities.layoutManager = layoutManager
        amenitiesAdapter =
            AmenitiesAdapter(requireContext(), busAmenities)
        binding.rvamenities.adapter = amenitiesAdapter
    }


}