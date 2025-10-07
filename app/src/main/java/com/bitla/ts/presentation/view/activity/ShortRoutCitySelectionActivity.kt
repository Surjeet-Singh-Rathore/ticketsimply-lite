package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.*
import java.util.*

class ShortRoutCitySelectionActivity : BaseActivity(), DialogSingleButtonListener, OnItemClickListener {
    
    private var bpdpList = mutableListOf<SearchModel>()
    private var filteredList = mutableListOf<SearchModel>()
    private lateinit var binding: ActivitySearchBinding
    private var citySelectionType: String =""
    private var resID: String =""
    private var selectedSource: String =""
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var layoutManagerCity: RecyclerView.LayoutManager
    private var searchAdapter: SearchAdapter? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        citySelectionType = intent.getStringExtra(getString(R.string.CITY_SELECTION_TYPE)) ?: ""
        resID = intent.getStringExtra(getString(R.string.res_id)) ?: ""
        selectedSource = intent.getStringExtra(getString(R.string.selected)) ?: ""

        setPref()
        bpdpList = PreferenceUtils.getBpDpList()
        setCityAdapter(bpdpList)
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        binding.toolbarImageLeft.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (bpdpList.isNotEmpty())
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

    override fun initUI() {

    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {
        filteredList = mutableListOf()

        for (s in bpdpList) {
            if (s.name != null && s.name!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filteredList.add(s)
            }
        }
        if (searchAdapter != null) searchAdapter?.filterList(filteredList)
    }

    private fun setPref(){
        loginModelPref = PreferenceUtils.getLogin()
    }

    private fun setCityAdapter(list: MutableList<SearchModel>) {

        layoutManagerCity = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManagerCity
        searchAdapter = SearchAdapter(
            this,
            this,
            list
        )
        binding.rvCity.adapter = searchAdapter
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun onSingleButtonClick(str: String) {
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        var temp = listOf<String>()
        if (view.tag.toString().contains("serviceSelection")) {
            temp = view.tag.toString().split("|")
        }

        val returnIntent = Intent()
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_NAME), temp[1])
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_ID), temp[2])
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_TYPE),citySelectionType)

        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }
}