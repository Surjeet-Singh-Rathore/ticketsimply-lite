package com.bitla.ts.presentation.view.activity

import android.annotation.*
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
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.*
import java.util.*


class SearchOriginDestinationActivity : BaseActivity(), OnItemClickListener {

    private var searchAdapter: SearchAdapter? = null
    private var busType: String = ""
    private lateinit var binding: ActivitySearchOriginDestBinding
    private var searchList : ArrayList<SearchModel> = arrayListOf()
    private var type : String = ""
    private var filterdNames: MutableList<SearchModel> = mutableListOf()


    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivitySearchOriginDestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getCitiesList()

        if(intent.hasExtra("type")){
            type = intent.getStringExtra("type")?:""
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
               // if (::searchList.isInitialized)
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

    private fun getCitiesList() {
        searchList.clear()
        searchList = PreferenceUtils.getOriginDestList()
        if(!searchList.isNullOrEmpty()){
            setCityAdapter()
        }


    }

    private fun setCityAdapter() {
        val layoutManagerCity = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManagerCity
        searchAdapter = SearchAdapter(
            this,
            this,
            searchList
        )
        binding.rvCity.adapter = searchAdapter
        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }


    @SuppressLint("DefaultLocale")
    private fun search(text: String) {
        filterdNames = mutableListOf<SearchModel>()

        for (s in searchList) {
            if (s.name != null && s.name!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filterdNames.add(s)
            }
        }
        if (searchAdapter != null) searchAdapter?.filterList(filterdNames)
    }
    
    
    override fun isInternetOnCallApisAndInitUI() {


    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
        val returnIntent = Intent()
        if(filterdNames.isEmpty()){
            returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_ID), searchList[position].id.toString())
            returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_NAME), searchList[position].name)

        }else{
            returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_ID), filterdNames[position].id.toString())
            returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_NAME), filterdNames[position].name)

        }
        returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_TYPE), type)
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