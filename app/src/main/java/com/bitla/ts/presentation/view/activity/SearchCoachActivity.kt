package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivitySearchCoachBinding
import com.bitla.ts.databinding.ActivityVehicleDetailsBinding
import com.bitla.ts.domain.pojo.get_coach_details.response.CoachDetailsResponse
import com.bitla.ts.domain.pojo.get_coach_details.response.CoachDetailsResponseItem
import com.bitla.ts.presentation.adapter.SearchAdapter
import com.bitla.ts.presentation.adapter.SearchCoachAdapter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.common.stringToJson
import com.bitla.ts.utils.constants.SEARCH_COACH_REQUEST_CODE
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import java.util.*

class SearchCoachActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchCoachBinding
    private lateinit var searchCoachAdapter: SearchCoachAdapter
    private var coachDetailsList = mutableListOf<CoachDetailsResponseItem>()
    private var filteredCoachList = mutableListOf<CoachDetailsResponseItem>()

    override fun initUI() {
        binding = ActivitySearchCoachBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        val coachDetailsResponse = PreferenceUtils.getObject<CoachDetailsResponse>("COACH_DETAILS_RESPONSE")
        if(coachDetailsResponse != null) {
            coachDetailsResponse.forEach {
                coachDetailsList.add(it)
            }
            setCoachAdapter()

            binding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    search(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
                ) {
                }
            })
        }

        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun search(text: String) {
        filteredCoachList = mutableListOf()


        for (s in coachDetailsList) {
            if (s.coachNumber != null && s.coachNumber.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                if(!filteredCoachList.contains(s)){
                    filteredCoachList.add(s)

                }
            }
        }


        if (::searchCoachAdapter.isInitialized) {
            searchCoachAdapter.filterList(filteredCoachList)
        }
    }

    private fun setCoachAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSearchCoach.layoutManager = layoutManager
        searchCoachAdapter = SearchCoachAdapter(this, coachDetailsList, onCoachSelected = { position, item ->
            val intent = Intent()

            intent.putExtra("coach_number", item.coachNumber)
            intent.putExtra("coach_id", item.coachId)

            setResult(SEARCH_COACH_REQUEST_CODE, intent)
            finish()
        })
        binding.rvSearchCoach.adapter = searchCoachAdapter
    }
}