package com.bitla.ts.presentation.view.activity.ticketDetails

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivitySelectRestaurantBinding
import com.bitla.ts.domain.pojo.mealCoupon.RestaurantList
import com.bitla.ts.domain.pojo.mealCoupon.RestaurantListResponse
import com.bitla.ts.presentation.adapter.RestaurantListAdapter
import com.bitla.ts.presentation.viewModel.RestaurantViewModel
import com.bitla.ts.utils.common.edgeToEdge
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import java.util.Locale

class SelectRestaurantActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectRestaurantBinding
    private lateinit var restaurantAdapter: RestaurantListAdapter
    private val viewModel by viewModel<RestaurantViewModel>()
    private var filteredRestaurantList = mutableListOf<RestaurantList>()
    private var restaurantListResponse = RestaurantListResponse()
    private var apiKey = ""
    override fun initUI() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        initui()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

     fun initui() {

        getIntentData()
        callRestaurantListApi()


        binding.etSearch.addTextChangedListener(object : TextWatcher {
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
        binding.toolbarImageLeft.setOnClickListener {
            navigateBack()
        }
    }

    private fun getIntentData() {
        apiKey= intent.extras?.get("apiKey").toString()
    }


    private fun navigateBack() {
        finish()
    }

    private fun callRestaurantListApi() {
        if (this.isNetworkAvailable()) {
            viewModel.getRestaurantListApi(apiKey)
        } else {
          toast(getString(R.string.network_not_available))
        }
    }



    private fun search(text: String) {
if(!restaurantListResponse.restaurantList.isNullOrEmpty()) {
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

    }

    private fun setObservers() {

        viewModel.restaurantListResponse.observe(this, Observer {
            if (it != null) {
                if (it.code == 200) {

                    val restaurantData = RestaurantList()
                    restaurantData.id = -1
                    restaurantData.restaurantName = getString(R.string.all_restaurants)
                    it.restaurantList?.add(0, restaurantData)

                    restaurantListResponse = it
                    if (it.restaurantList?.size!! > 0) {
                        binding.progressBarList.visibility = View.GONE
                        setRestaurantListAdapter(it.restaurantList!!)
                    }
                }
            } else {
               toast(getString(R.string.server_error))
            }
        })

    }


    private fun setRestaurantListAdapter(restaurantList: ArrayList<RestaurantList>) {
        restaurantAdapter =
            RestaurantListAdapter(this) { position -> setClickedResult(position) }
        restaurantAdapter.addData(restaurantList)
        binding.listRv.adapter = restaurantAdapter
    }

    private fun setClickedResult(data: String) {
        intent.putExtra("selectedRestaurant", data)
        setResult(RESULT_OK, intent)
        finish()
    }


}