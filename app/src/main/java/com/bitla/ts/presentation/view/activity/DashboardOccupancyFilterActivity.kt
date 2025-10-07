package com.bitla.ts.presentation.view.activity

import android.app.*
import android.content.*
import android.os.Build
import android.text.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.city_details.response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.*
import java.util.*

class DashboardOccupancyFilterActivity : BaseActivity(), OnItemClickListener,
    DialogSingleButtonListener {

    private lateinit var binding: ActivitySearchBinding
    private var cityList = mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var citylistAdapter: CityListAdapterNew
    private var currentSelection = ""
    private var currentSelectedId = 0
    private var filteredCityList: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result> =
        mutableListOf()


    override fun initUI() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        var temp = PreferenceUtils.getObject<CityDetailsResponseModel>("cityListModel")
        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        if (temp != null) {
            cityList = temp.result
        }
        currentSelection = intent.getStringExtra("currentSelection") ?: ""
        if (currentSelection == "destination") {
            currentSelectedId = intent.getIntExtra("currentSelectedId", 0)
            cityList.removeIf {
                it.id == currentSelectedId
            }
        }
        setCityAdapter()

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
    }
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun search(text: String) {
        filteredCityList = mutableListOf()
        for (s in cityList) {
            if (s.name != null && s.name.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filteredCityList.add(s)
            }
        }
        if (citylistAdapter != null) citylistAdapter.filterList(filteredCityList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)

    }
    
    override fun onSingleButtonClick(str: String) {

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        val returnIntent = Intent()

        returnIntent.putExtra(getString(R.string.SELECTED_CITY_NAME), data)
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_ID), position)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    private fun setCityAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManager
        citylistAdapter =
            CityListAdapterNew(this, this, cityList)
        binding.rvCity.adapter = citylistAdapter
    }
}